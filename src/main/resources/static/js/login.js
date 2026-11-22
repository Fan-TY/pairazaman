document.addEventListener('DOMContentLoaded', function () {
    const boton = document.getElementById('togglePassword');
    const campo = document.getElementById('password');
    if (!boton || !campo) {
        return;
    }
    boton.addEventListener('click', function () {
        const visible = campo.type === 'text';
        campo.type = visible ? 'password' : 'text';
        boton.querySelector('i').className = visible ? 'bi bi-eye' : 'bi bi-eye-slash';
        boton.setAttribute('aria-label', visible ? 'Mostrar contraseña' : 'Ocultar contraseña');
    });
});