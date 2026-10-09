-- Schema-only export for fitforge_db.
-- Row data has been left out.
CREATE DATABASE IF NOT EXISTS `fitforge_db`;
USE `fitforge_db`;

CREATE TABLE `badminton_logs` (
  `Badmintonlog_id` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Session_type` varchar(100) DEFAULT NULL,
  `Duration_Minutes` float DEFAULT NULL,
  `Calorie_burn` float DEFAULT NULL,
  `Workout_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `badminton_targets` (
  `Target_ID` int(11) NOT NULL,
  `User_id` int(11) DEFAULT NULL,
  `Goal_Sessions` int(11) DEFAULT NULL,
  `Goal_Calories` int(11) DEFAULT NULL,
  `Goal_Month` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `cycling_logs` (
  `Cycling_log_id` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Route_Name` varchar(100) DEFAULT NULL,
  `Total_Distance` float DEFAULT NULL,
  `Speed` float DEFAULT NULL,
  `Calorie_burn` float DEFAULT NULL,
  `Workout_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `cycling_targets` (
  `Target_id` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Goal_Distance` float DEFAULT NULL,
  `Goal_Calorie` float DEFAULT NULL,
  `Goal_Month` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `running_targets` (
  `Goal_ID` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Goal_Distance` float DEFAULT NULL,
  `Goal_Calorie` float DEFAULT NULL,
  `Goal_Month` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `run_logs` (
  `Runlog_id` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Route_Name` varchar(100) DEFAULT NULL,
  `Total_Distance` float DEFAULT NULL,
  `Speed` float DEFAULT NULL,
  `Calorie_burn` float DEFAULT NULL,
  `Workout_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `user_reg_pf` (
  `ID` int(11) NOT NULL,
  `Full_Name` varchar(50) NOT NULL,
  `User_Name` varchar(50) NOT NULL,
  `Date_of_Birth` varchar(50) NOT NULL,
  `Gender` enum('Male','Female') NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `Weight(kg)` float NOT NULL,
  `Height(cm)` float NOT NULL,
  `Registration_date` datetime NOT NULL,
  `Isverified` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `weight_logs` (
  `Weight_ID` int(11) NOT NULL,
  `User_ID` int(11) NOT NULL,
  `Exercise_Name` varchar(100) DEFAULT NULL,
  `Weight` float NOT NULL,
  `Reps` int(11) NOT NULL,
  `Total_Sets` int(11) DEFAULT NULL,
  `Total_Volume` float DEFAULT NULL,
  `Calories_Burned` float DEFAULT NULL,
  `Workout_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `weight_targets` (
  `Weight_Target_ID` int(11) NOT NULL,
  `User_ID` int(11) DEFAULT NULL,
  `Goal_Sets` float DEFAULT NULL,
  `Goal_Calories_Burned` float DEFAULT NULL,
  `Goal_Month` varchar(7) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `badminton_logs`
  ADD PRIMARY KEY (`Badmintonlog_id`),
  ADD KEY `User_ID` (`User_ID`);

ALTER TABLE `badminton_targets`
  ADD PRIMARY KEY (`Target_ID`),
  ADD UNIQUE KEY `uniq_user_month` (`User_id`,`Goal_Month`);

ALTER TABLE `cycling_logs`
  ADD PRIMARY KEY (`Cycling_log_id`),
  ADD KEY `User_ID` (`User_ID`);

ALTER TABLE `cycling_targets`
  ADD PRIMARY KEY (`Target_id`),
  ADD UNIQUE KEY `uniq_user_month` (`User_ID`,`Goal_Month`);

ALTER TABLE `running_targets`
  ADD PRIMARY KEY (`Goal_ID`),
  ADD UNIQUE KEY `uniq_user_month` (`User_ID`,`Goal_Month`);

ALTER TABLE `run_logs`
  ADD PRIMARY KEY (`Runlog_id`),
  ADD KEY `User_ID` (`User_ID`);

ALTER TABLE `user_reg_pf`
  ADD PRIMARY KEY (`ID`);

ALTER TABLE `weight_logs`
  ADD PRIMARY KEY (`Weight_ID`),
  ADD KEY `User_ID` (`User_ID`);

ALTER TABLE `weight_targets`
  ADD PRIMARY KEY (`Weight_Target_ID`),
  ADD UNIQUE KEY `uniq_user_month` (`User_ID`,`Goal_Month`);

ALTER TABLE `badminton_logs`
  MODIFY `Badmintonlog_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

ALTER TABLE `badminton_targets`
  MODIFY `Target_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

ALTER TABLE `cycling_logs`
  MODIFY `Cycling_log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

ALTER TABLE `cycling_targets`
  MODIFY `Target_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `running_targets`
  MODIFY `Goal_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

ALTER TABLE `run_logs`
  MODIFY `Runlog_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

ALTER TABLE `user_reg_pf`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

ALTER TABLE `weight_logs`
  MODIFY `Weight_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

ALTER TABLE `weight_targets`
  MODIFY `Weight_Target_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

ALTER TABLE `badminton_logs`
  ADD CONSTRAINT `badminton_logs_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`) ON DELETE CASCADE;

ALTER TABLE `badminton_targets`
  ADD CONSTRAINT `badminton_targets_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user_reg_pf` (`ID`);

ALTER TABLE `cycling_logs`
  ADD CONSTRAINT `cycling_logs_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`) ON DELETE CASCADE;

ALTER TABLE `cycling_targets`
  ADD CONSTRAINT `cycling_targets_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `user_reg_pf` (`ID`);

ALTER TABLE `running_targets`
  ADD CONSTRAINT `running_targets_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`);

ALTER TABLE `run_logs`
  ADD CONSTRAINT `run_logs_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`) ON DELETE CASCADE;

ALTER TABLE `weight_logs`
  ADD CONSTRAINT `weight_logs_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`) ON DELETE CASCADE;

ALTER TABLE `weight_targets`
  ADD CONSTRAINT `weight_targets_ibfk_1` FOREIGN KEY (`User_ID`) REFERENCES `user_reg_pf` (`ID`);
