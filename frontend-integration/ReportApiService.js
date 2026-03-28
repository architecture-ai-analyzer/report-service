// API Service para integrar com o Report Service backend
export class ReportApiService {
    private static BASE_URL = 'http://localhost:8080/api';

    // Gera relatório para um upload específico
    static async generateReport(uploadId, fileName, userId = 'default-user') {
        try {
            const response = await fetch(`${this.BASE_URL}/reports/${uploadId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: userId,
                    fileName: fileName
                })
            });

            if (!response.ok) {
                throw new Error(`Failed to generate report: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error generating report:', error);
            throw error;
        }
    }

    // Obtém relatório específico
    static async getReport(uploadId) {
        try {
            const response = await fetch(`${this.BASE_URL}/reports/${uploadId}`);
            
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Relatório não encontrado');
                }
                throw new Error(`Failed to get report: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error getting report:', error);
            throw error;
        }
    }

    // Lista todos os relatórios
    static async listReports(page = 0, size = 10) {
        try {
            const response = await fetch(`${this.BASE_URL}/reports?page=${page}&size=${size}`);
            
            if (!response.ok) {
                throw new Error(`Failed to list reports: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error listing reports:', error);
            throw error;
        }
    }

    // Obtém status do processamento
    static async getProcessingStatus(uploadId) {
        try {
            const response = await fetch(`${this.BASE_URL}/reports/${uploadId}/status`);
            
            if (!response.ok) {
                throw new Error(`Failed to get status: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error getting processing status:', error);
            throw error;
        }
    }

    // Faz download do relatório
    static async downloadReport(uploadId, fileName) {
        try {
            const response = await fetch(`${this.BASE_URL}/reports/${uploadId}/download`);
            
            if (!response.ok) {
                throw new Error(`Failed to download report: ${response.statusText}`);
            }

            // Criar blob e fazer download
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `report-${uploadId}.pdf`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
            
            return { success: true };
        } catch (error) {
            console.error('Error downloading report:', error);
            throw error;
        }
    }

    // Verifica se relatório está pronto
    static async isReportReady(uploadId) {
        try {
            const status = await this.getProcessingStatus(uploadId);
            return status.status === 'ANALISADO';
        } catch (error) {
            return false;
        }
    }
}

export default ReportApiService;
