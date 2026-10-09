<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);


require 'connection_test.php';
if (isset($_POST['phpFunction'])) {

    if ($_POST['phpFunction'] == 'Save_cycleData') {
        SaveCycleData();
    }

    if ($_POST['phpFunction'] == 'get_cyclelogs') {
        GetCycleLogs();
    }
    if ($_POST['phpFunction'] == 'Get_Cycling_Monthly_Compare') {
        GetCyclingMonthlyCompare();
    }
}

function SaveCycleData() {
    global $connection;
    $user_id = $_POST['User_ID'];
    $route_name = $_POST['Route_Name'];
    $total_distance = $_POST['Total_Distance'];
    $speed = $_POST['Speed'];
    $cal_burn = $_POST['Calorie_burn'];
    
    if (!$connection) {
        die("Database connection failed: " . mysqli_connect_error());
    }
       
    $sql = "INSERT INTO cycling_logs (`User_ID`, `Route_Name`, `Total_Distance`, `Speed` , `Calorie_burn`) 
            VALUES ('$user_id', '$route_name', '$total_distance', '$speed','$cal_burn')";

    if(mysqli_query($connection,$sql)){
        echo "true";
    } else {
        echo mysqli_error($connection);
        return;
    }
    mysqli_close($connection);
}

//Fetch RunLogs Data
function GetCycleLogs() {
    global $connection; // Use global connection from connection_test.php
    
    $user_id = $_POST['User_ID'];
    // Secure query using prepared statements
    $sql = "SELECT `Route_Name`, `Total_Distance`, `Speed`, `Calorie_burn`, `Workout_date`
            FROM cycling_logs 
            WHERE User_ID = ? 
            ORDER BY Workout_date DESC";

    $stmt = $connection->prepare($sql);
    if (!$stmt) {
        die(json_encode(["error" => "SQL Prepare failed: " . $connection->error]));
    }

    $stmt->bind_param("i", $user_id); // Ensure "i" (integer) matches the variable
    $stmt->execute();
    $result = $stmt->get_result();

    $cycling_logs = [];

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $cycling_logs[] = $row;
        }
    }

    // Return JSON response
    header('Content-Type: application/json');
    echo json_encode($cycling_logs);

    $stmt->close();
    $connection->close();
}
//Monthly Workout Summary and compare from run_logs and goals
function GetCyclingMonthlyCompare() {
    global $connection;

    header('Content-Type: application/json');

    if (!isset($_POST['User_ID'])) {
        echo json_encode(["error" => "User_ID is missing"]);
        exit();
    }

    $user_id = $_POST['User_ID'];

    $sql = "SELECT 
            c.Goal_Month AS month,
            c.Goal_Distance,
            c.Goal_Calorie,
            IFNULL(SUM(l.Total_Distance), 0) AS total_distance,
            IFNULL(SUM(l.Calorie_burn), 0) AS total_calories
        FROM cycling_targets c
        LEFT JOIN cycling_logs l 
            ON c.User_ID = l.User_ID 
            AND DATE_FORMAT(l.Workout_date, '%Y-%m') = c.Goal_Month
        WHERE c.User_ID = '$user_id'
        GROUP BY c.Goal_Month
        ORDER BY c.Goal_Month ASC";

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