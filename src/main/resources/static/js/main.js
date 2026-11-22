document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });

    const path = window.location.pathname;
    document.querySelectorAll('#navbarContent .navbar-nav .nav-link[href]').forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && href !== '#' && (path === href || path.startsWith(href + '/'))) {
            link.classList.add('active');
        }
    });
});