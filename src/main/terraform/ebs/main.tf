provider "aws" {
  region = "eu-west-3"
}

terraform {
  backend "s3" {
    bucket = "test-assignment-s3-bucket"
    key    = "testassignment-application/ebs-terraform.tfstate"
    region = "eu-west-3"
  }
}

resource "aws_elastic_beanstalk_application" "app" {
  name        = "test-assignment-application"
  description = "Sample app for the test assignment"
}

resource "aws_elastic_beanstalk_environment" "production" {
  name                = "app-production"
  application         = "${aws_elastic_beanstalk_application.app.name}"
  solution_stack_name = "64bit Amazon Linux 2017.09 v2.6.6 running Java 8"

  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = "vpc-877e81ee"
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = "subnet-6ff1c325, subnet-aea9b5d6, subnet-0588736c"
  }

  setting {
    namespace = "aws:ec2:vpc"
    name      = "AssociatePublicIpAddress"
    value     = "true"
  }

  setting {
    namespace = "aws:elasticbeanstalk:healthreporting:system"
    name      = "SystemType"
    value     = "enhanced"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application"
    name      = "Application Healthcheck URL"
    value     = "HTTP:8080/actuator/info"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "EC2KeyName"
    value     = "damien-eu-west-3"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t2.medium"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "RootVolumeType"
    value     = "gp2"                                 # general purpose SSD
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "RootVolumeSize"
    value     = "10"
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = "${aws_iam_instance_profile.eb_service_profile.arn}"
  }

  tags {
    Name = "test-assignment"
  }
}

resource "aws_iam_instance_profile" "eb_service_profile" {
  name = "app-instance-profile"
  path = "/"
  role = "${aws_iam_role.eb_role.name}"
}

resource "aws_iam_role" "eb_role" {
  name               = "app-eb-role"
  path               = "/"
  assume_role_policy = "${data.aws_iam_policy_document.ec2_instance_policy.json}"
}

data "aws_iam_policy_document" "ec2_instance_policy" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy_attachment" "eb_attachment" {
  count      = "${length(var.iam_policies)}"
  role       = "${aws_iam_role.eb_role.name}"
  policy_arn = "${lookup(var.iam_policies, count.index)}"
}

variable "iam_policies" {
  type = "map"

  default = {
    "0" = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
    "1" = "arn:aws:iam::aws:policy/AWSElasticBeanstalkMulticontainerDocker"
    "2" = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWorkerTier"
    "3" = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
  }
}
