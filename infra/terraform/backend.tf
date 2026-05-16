terraform {
  backend "s3" {
    bucket = "report-service-bucket-ai-analyzer"
    key    = "v1/report-service/dev/terraform.tfstate"
    region = "us-east-2"
  }
}