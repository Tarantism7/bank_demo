// Main page: the admin invite code is only needed when "Admin" is selected in the sign-up form.
(function () {
    var radios = document.querySelectorAll('input[name="accountType"]');
    var field = document.getElementById('inviteField');
    var input = document.getElementById('inviteCode');
    if (!radios.length || !field || !input) {
        return;
    }

    function sync() {
        var admin = document.querySelector('input[name="accountType"]:checked').value === 'admin';
        field.classList.toggle('hidden', !admin);
        input.required = admin;
        if (!admin) {
            input.value = '';
        }
    }

    radios.forEach(function (r) { r.addEventListener('change', sync); });
    sync();
})();
