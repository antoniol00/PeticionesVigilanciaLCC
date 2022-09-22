function validateLoginForm() {
    let name = document.forms["adminlogin"]["username"].value;
    let pass = document.forms["adminlogin"]["password"].value;

    if (name == '' | pass == '') {
        document.getElementById("faltaLogin").hidden = false;
        return false;
    }
    return true;
}