resource "kubernetes_namespace" "report_service" {
  metadata {
    name = "${var.app_name}-${var.environment}"

    labels = {
      name = var.app_name
    }
  }
}
