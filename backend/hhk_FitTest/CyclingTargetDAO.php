<?php
require 'connection_test.php';
if($_POST['phpFunction']== 'SaveCyclingGoal'){
    SaveCyclingGoal();
}

function SaveCyclingGoal() {
    global $connection;
    $user_id = $_POST['User_ID'] ?? 0;
    $goal_distance = $_POST['Goal_Distance'] ?? 0;
    $goal_calorie = $_POST['Goal_Calorie'] ?? 0;
    $goal_month = date('Y-m');
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO cycling_targets(User_ID, Goal_Distance, Goal_Calorie, Goal_Month)
        VALUES('$user_id', '$goal_distance', '$goal_calorie', '$goal_month')
        ON DUPLICATE KEY UPDATE
            Goal_Distance = VALUES(Goal_Distance),
            Goal_Calorie  = VALUES(Goal_Calorie)";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}
?>
