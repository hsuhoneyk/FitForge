<?php 
if ($_POST['phpFunction'] == 'createUser') { 
    createUser(); 
}

function createUser() {

    $fullname = $_POST['Full_Name'];
    $username = $_POST['User_Name'];
    $DoB = $_POST['Date_of_Birth'];
    $gender = $_POST['Gender'];
    $email = $_POST['Email'];
    $password = $_POST['Password'];
    $weight = $_POST['Weight(kg)'];
    $height = $_POST['Height(cm)'];

    // Hash the password
    $pass = md5($password);

    include "connection_test.php";

    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }

    // Check if email already exists
    $sql = "SELECT * FROM user_reg_pf WHERE Email = '$email'";
    $query = mysqli_query($connection, $sql);

    if (mysqli_num_rows($query) > 0) {
        echo "An account with this email already exists. Please SignUp again!";
        return;
    }

    // Corrected INSERT (column name is IsVerified)
    $sql = "INSERT INTO user_reg_pf 
        (Full_Name, User_Name, Date_of_Birth, Gender, Email, Password, `Weight(kg)`, `Height(cm)`, Registration_date, Isverified)
        VALUES 
        ('$fullname', '$username', '$DoB', '$gender', '$email', '$pass', '$weight', '$height', NOW(), '1')";

    if (mysqli_query($connection, $sql)) {
        echo "true";
    } else {
        echo mysqli_error($connection);
    }

    mysqli_close($connection);
}
?>