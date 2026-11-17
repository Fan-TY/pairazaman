// Scripts generales del cliente para Pairazamán MVC
document.addEventListener('DOMContentLoaded', function () {
    // Auto-cierre de alertas flash después de 5 segundos
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });
});
