terraform {
  backend "s3" {
    bucket         = "tf-state-ai-architecture-analyzer"
    key            = "v1/report-service/dev/terraform.tfstate"
    region         = "us-east-2"
  }
}
