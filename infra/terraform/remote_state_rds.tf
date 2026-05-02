data "terraform_remote_state" "rds" {
  backend = "s3"

  config = {
    bucket = "tf-state-challenge-bucket"
    key    = local.rds_state_path
    region = "us-east-2"
  }
}