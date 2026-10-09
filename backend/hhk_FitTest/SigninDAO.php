<?php
require_once 'connection_test.php';
// Get data from form
if(isset($_POST['phpFunction'])){
    if($_POST['phpFunction']=='login'){
        login();
    }
}
function login() {
    //session_start();
    global $connection;
    $userName = $_POST['User_Name'];
    $psWord = md5($_POST['Password']);
    
    // Prepare SQL 
    $sql = "SELECT `ID`,`Full_Name`,`Weight(kg)`,`Height(cm)` From  `user_reg_pf` WHERE Email='".$userName."' AND Password='".$psWord."'";

    //include "connection_test.php";

    $res = mysqli_query($connection,$sql);
    $num_row = mysqli_num_rows($res);
    $row=mysqli_fetch_assoc($res);

    if($num_row == 1){
        $response = $row;  // Assign the fetched data to response array
        $response["response"] = "true"; // Add response status

        echo '{"userid":"'.$row['ID'].'","FullName":"'.$row['Full_Name'].'","Weight":"'.$row['Weight(kg)'].'","Height":"'.$row['Height(cm)'].'","response":"true"}';
        
        //$_SESSION['uName'] = $uName;
       // $_SESSION['userid'] = $row['id'];        
    }
    else{
        echo '{"response":"false"}';
    }
}
?>
