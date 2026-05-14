resource "kubernetes_namespace" "report_service" {
  metadata {
    name = "report-service-${local.environment}"

    labels = {
      name = "report-service"
    }
  }

  depends_on = [
    data.terraform_remote_state.eks
  ]
}
