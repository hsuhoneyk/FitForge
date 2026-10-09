<?php
require 'connection_test.php';
if($_POST['phpFunction']== 'SaveBadmintonGoal'){
    SaveBadmintonGoal();
}

function SaveBadmintonGoal() {
    global $connection;
    
    $user_id = $_POST['User_id'];
    $goal_sessions = $_POST['Goal_Sessions'];
    $goal_calories = $_POST['Goal_Calories'];
    $goal_month = $_POST['Goal_Month'];;
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO badminton_targets(`User_id`, `Goal_Sessions`, `Goal_Calories`, `Goal_Month`)
        VALUES('$user_id', '$goal_sessions' , '$goal_calories' , '$goal_month')
        ON DUPLICATE KEY UPDATE
            Goal_Sessions = VALUES(Goal_Sessions),
            Goal_Calories = VALUES(Goal_Calories)";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}
?>
