resource "kubernetes_horizontal_pod_autoscaler" "report_service_hpa" {
	metadata {
		name      = "report-service-hpa"
		namespace = kubernetes_namespace.report_service.metadata[0].name
		labels = {
			app = "report-service"
		}
	}

	spec {
		min_replicas = 2
		max_replicas = 4

		scale_target_ref {
			api_version = "apps/v1"
			kind        = "Deployment"
			name        = kubernetes_deployment.report_service_app.metadata[0].name
		}

		target_cpu_utilization_percentage = 70
	}
}
