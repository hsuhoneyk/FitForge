<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);


require 'connection_test.php';
if (isset($_POST['phpFunction'])) {

    if ($_POST['phpFunction'] == 'Save_Badminton') {
        Save_Badminton();
    }

    if ($_POST['phpFunction'] == 'Get_Badminton_Logs') {
        Get_Badminton_Logs();
    }
    if ($_POST['phpFunction'] == 'Get_Badminton_Monthly_Compare') {
        GetBadmintonMonthlyCompare();
    }
}

function Save_Badminton() {
    global $connection;
    $user_id = $_POST['User_ID'];
    $session_type = $_POST['Session_type'];
    $duration_minutes = $_POST['Duration_Minutes'];
    $calorie_burn = $_POST['Calorie_burn'];
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO badminton_logs (`User_ID`, `Session_type`, `Duration_Minutes`, `Calorie_burn`, `Workout_date`) 
            VALUES ('$user_id', '$session_type', '$duration_minutes ', '$calorie_burn', NOW())";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}

//Fetch RunLogs Data
function Get_Badminton_Logs() {
    global $connection; // Use global connection from connection_test.php
    
    $user_id = $_POST['User_ID'];
    
    $sql = "SELECT `Session_type`, `Duration_Minutes`, `Calorie_burn`, `Workout_date`
            FROM badminton_logs 
            WHERE User_ID = ? 
            ORDER BY Workout_date DESC";

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
function GetBadmintonMonthlyCompare() {
    global $connection;

    header('Content-Type: application/json');

    if (!isset($_POST['User_ID'])) {
        echo json_encode(["error" => "User_ID is missing"]);
        exit();
    }

    $user_id = $_POST['User_ID'];

    $sql = "SELECT 
            w.Goal_Month AS month,
            w.Goal_Sessions,
            w.Goal_Calories,
            COUNT(b.Session_type) AS total_session,
            IFNULL(SUM(b.Calorie_burn), 0) AS total_calorie
        FROM badminton_targets w
        LEFT JOIN badminton_logs b 
            ON w.User_id = b.User_ID 
            AND DATE_FORMAT(b.Workout_date, '%Y-%m') = w.Goal_Month
        WHERE w.User_id = '$user_id'
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