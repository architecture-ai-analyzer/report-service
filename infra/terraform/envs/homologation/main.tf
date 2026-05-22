# Configure the environment as 'homologation'
locals {
  environment = "dev"
}

# Get current AWS account ID for SQS queue URLs
data "aws_caller_identity" "current" {}

module "rds" {
  source = "../../modules/rds"

  database_name         = "reportdb"
  db_username           = "postgres"
  db_password           = var.rds_password
  instance_class        = var.rds_instance_class
  allocated_storage     = var.rds_allocated_storage
  engine_version        = "16"
  backup_retention_days = 7
  skip_final_snapshot   = true
  environment           = local.environment
  project_name          = "report-service"
  vpc_id                = data.terraform_remote_state.vpc.outputs.vpc_id
  private_db_subnet_ids = data.terraform_remote_state.vpc.outputs.private_db_subnet_ids
  security_group_name   = "report-service-rds-sg-${local.environment}"
}

module "k8s" {
  source = "../../modules/k8s"

  environment                 = local.environment
  container_image             = var.container_image
  app_name                    = var.app_name
  app_port                    = var.app_port
  aws_region                  = "us-east-2"
  rds_jdbc_url                = module.rds.jdbc_url
  rds_username                = module.rds.username
  rds_password                = var.rds_password
  aws_access_key_id           = var.aws_access_key_id
  aws_secret_access_key       = var.aws_secret_access_key
  aws_status_update_queue_url = "https://sqs.us-east-2.amazonaws.com/${data.aws_caller_identity.current.account_id}/status-update-queue"
}

# Data sources for remote state
data "terraform_remote_state" "vpc" {
  backend = "s3"

  config = {
    bucket         = "tf-state-ai-architecture-analyzer"
    key            = "v1/networking/${local.environment}/terraform.tfstate"
    region         = "us-east-2"
    dynamodb_table = "tf-state-lock"
    encrypt        = true
  }
}

data "terraform_remote_state" "eks" {
  backend = "s3"

  config = {
    bucket         = "tf-state-ai-architecture-analyzer"
    key            = "v1/eks/${local.environment}/terraform.tfstate"
    region         = "us-east-2"
    dynamodb_table = "tf-state-lock"
    encrypt        = true
  }
}
