<?php
require 'connection_test.php';
if($_POST['phpFunction']== 'SaveWeightliftingGoal'){
    SaveWeightliftingGoal();
}

function SaveWeightliftingGoal() {
    global $connection;
    
    $user_id = $_POST['User_ID'];
    $goal_month = date('Y-m');
    $goal_sets = $_POST['Goal_Sets'];
    $goal_calories_burned = $_POST['Goal_Calories_Burned'];
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO weight_targets(User_ID, Goal_Sets, Goal_Calories_Burned, Goal_Month)
        VALUES('$user_id', $goal_sets , $goal_calories_burned , '$goal_month')
        ON DUPLICATE KEY UPDATE
            Goal_Sets = VALUES(Goal_Sets),
            Goal_Calories_Burned  = VALUES(Goal_Calories_Burned)";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}
?>
