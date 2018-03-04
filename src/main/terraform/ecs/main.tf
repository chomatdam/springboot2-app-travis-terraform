provider "aws" {
  region = "eu-west-3"
}

terraform {
  backend "s3" {
    bucket = "test-assignment-s3-bucket"
    key    = "testassignment-application/ecs-terraform.tfstate"
    region = "eu-west-3"
  }
}

module "ecs-cluster" {
  source        = "github.com/terraform-community-modules/tf_aws_ecs"
  name          = "test-assignment-services"
  servers       = 1
  vpc_id        = "vpc-877e81ee"
  subnet_id     = ["subnet-6ff1c325, subnet-aea9b5d6, subnet-0588736c"]
  key_name      = "damien-eu-west-3"
  instance_type = "t2.medium"
}
