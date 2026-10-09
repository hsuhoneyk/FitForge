<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);


require 'connection_test.php';
if (isset($_POST['phpFunction'])) {

    if ($_POST['phpFunction'] == 'Save_Weightlifting') {
        Save_Weightlifting();
    }

    if ($_POST['phpFunction'] == 'Get_Weightlifting_Logs') {
        Get_Weightlifting_Logs();
    }
    if ($_POST['phpFunction'] == 'Get_Weightlifting_Monthly_Compare') {
        GetWeightliftingMonthlyCompare();
    }
}

function Save_Weightlifting() {
    global $connection;
     $user_id = $_POST['User_ID'];
    $exercise_name = $_POST['Exercise_Name'];
    $weight = $_POST['Weight'];
    $reps = $_POST['Reps'];
    $total_sets = $_POST['Total_Sets'];
    $total_volume = $_POST['Total_Volume'];
    $calories_burned = $_POST['Calories_Burned'];
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO weight_logs (`User_ID`, `Exercise_Name`, `Weight`, `Reps`, `Total_Sets`, `Total_Volume`, `Calories_Burned`, `Workout_Date`) 
            VALUES ('$user_id', '$exercise_name', $weight, $reps, '$total_sets', '$total_volume', '$calories_burned', NOW())";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}

//Fetch RunLogs Data
function Get_Weightlifting_Logs() {
    global $connection; // Use global connection from connection_test.php
    
    $user_id = $_POST['User_ID'];
    
    $sql = "SELECT `Exercise_Name`, `Weight`, `Reps`, `Total_Sets`, `Total_Volume`, `Calories_Burned`, `Workout_Date`
            FROM weight_logs 
            WHERE User_ID = ? 
            ORDER BY Workout_Date DESC";

    $stmt = $connection->prepare($sql);
    if (!$stmt) {
        die(json_encode(["error" => "SQL Prepare failed: " . $connection->error]));
    }

    $stmt->bind_param("i", $user_id); // Ensure "i" (integer) matches the variable
    $stmt->execute();
    $result = $stmt->get_result();

    $weight_logs = [];

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $weight_logs[] = $row;
        }
    }

    // Return JSON response
    header('Content-Type: application/json');
    echo json_encode($weight_logs);

    $stmt->close();
    $connection->close();
}
//Monthly Workout Summary and compare from run_logs and goals
function GetWeightliftingMonthlyCompare() {
    global $connection;

    header('Content-Type: application/json');

    if (!isset($_POST['User_ID'])) {
        echo json_encode(["error" => "User_ID is missing"]);
        exit();
    }

    $user_id = $_POST['User_ID'];

    $sql = "SELECT 
            w.Goal_Month AS month,
            w.Goal_Sets,
            w.Goal_Calories_Burned,
            IFNULL(SUM(l.Total_Sets), 0) AS total_sets,
            IFNULL(SUM(l.Calories_Burned), 0) AS total_calories
        FROM weight_targets w
        LEFT JOIN weight_logs l 
            ON w.User_ID = l.User_ID 
            AND DATE_FORMAT(l.Workout_Date, '%Y-%m') = w.Goal_Month
        WHERE w.User_ID = '$user_id'
        GROUP BY w.Goal_Month
        ORDER BY w.Goal_Month ASC";

    $result = mysqli_query($connection, $sql);

    if (!$result) {
        echo json_encode([
            "error" => "SQL Error",
            "message" => mysqli_error($connection)
        ]);
        exit();
    }

    $data = [];

    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }

    echo json_encode($data);
}
?>