locals {
  rds_state_path = "rds/${var.environment}/terraform.tfstate"
}

locals {
  infra_kubernetes_state_path = "kubernetes/${var.environment}/terraform.tfstate"
}