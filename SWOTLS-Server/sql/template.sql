CREATE TABLE `arenas` (
  `arena_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `location` varchar(50) COLLATE utf16_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `contestant-tournament` (
  `entry_id` int(11) NOT NULL,
  `team_id` int(11) DEFAULT NULL,
  `contestant_id` int(11) DEFAULT NULL,
  `tournament_id` int(11) NOT NULL,
  `score` int(11) DEFAULT '0',
  `league` int(11) DEFAULT '1',
  `starting_position` int(11) DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=latin2;
CREATE TABLE `contestants` (
  `contestant_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `surname` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `nickname` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `score` int(11) NOT NULL DEFAULT '1200',
  `language` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `contact_info` text COLLATE utf16_polish_ci NOT NULL,
  `additional_info` text COLLATE utf16_polish_ci,
  `team_id` int(11) DEFAULT NULL,
  `rounds` int(11) DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `matches` (
  `match_id` int(11) NOT NULL,
  `sideA` int(11) DEFAULT NULL,
  `sideB` int(11) DEFAULT NULL,
  `teamA` int(11) DEFAULT NULL,
  `teamB` int(11) DEFAULT NULL,
  `sideA_score` int(11) DEFAULT NULL,
  `sideB_score` int(11) DEFAULT NULL,
  `arena_id` int(11) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `tournament` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `reports` (
  `report_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `tournament_id` int(11) NOT NULL,
  `report_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin2;
CREATE TABLE `system_users` (
  `sys_usr_id` int(11) NOT NULL,
  `login` varchar(20) COLLATE utf16_polish_ci NOT NULL,
  `pw_hash` varchar(64) COLLATE utf16_polish_ci NOT NULL,
  `permissions` varchar(10) COLLATE utf16_polish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `teams` (
  `team_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `where_from` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `leader_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `tournaments` (
  `tournament_id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf16_polish_ci NOT NULL,
  `type` enum('solo','team') COLLATE utf16_polish_ci NOT NULL,
  `operator` int(11) NOT NULL,
  `additional_info` text COLLATE utf16_polish_ci,
  `system` int(11) NOT NULL,
  `season` int(11) NOT NULL DEFAULT '1',
  `stage` int(11) NOT NULL DEFAULT '0',
  `rounds` int(11) DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;

ALTER TABLE `arenas`
  ADD PRIMARY KEY (`arena_id`);

ALTER TABLE `contestant-tournament`
  ADD PRIMARY KEY (`entry_id`),
  ADD KEY `contestant_id` (`contestant_id`),
  ADD KEY `tournament_id` (`tournament_id`),
  ADD KEY `team_id` (`team_id`);

ALTER TABLE `contestants`
  ADD PRIMARY KEY (`contestant_id`),
  ADD UNIQUE KEY `contestant_id` (`contestant_id`),
  ADD UNIQUE KEY `nickname` (`nickname`),
  ADD KEY `team_id` (`team_id`),
  ADD KEY `surname` (`surname`);

ALTER TABLE `matches`
  ADD PRIMARY KEY (`match_id`),
  ADD KEY `sideA` (`sideA`),
  ADD KEY `arena_id` (`arena_id`),
  ADD KEY `sideB` (`sideB`),
  ADD KEY `tournament` (`tournament`),
  ADD KEY `teamA` (`teamA`),
  ADD KEY `teamB` (`teamB`);

ALTER TABLE `reports`
  ADD PRIMARY KEY (`report_id`),
  ADD KEY `tournament_id` (`tournament_id`);

ALTER TABLE `system_users`
  ADD PRIMARY KEY (`sys_usr_id`),
  ADD UNIQUE KEY `login` (`login`);

ALTER TABLE `teams`
  ADD PRIMARY KEY (`team_id`),
  ADD KEY `leader_id` (`leader_id`);

ALTER TABLE `tournaments`
  ADD PRIMARY KEY (`tournament_id`),
  ADD KEY `operator` (`operator`);

ALTER TABLE `arenas`
  MODIFY `arena_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `contestant-tournament`
  MODIFY `entry_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `contestants`
  MODIFY `contestant_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `matches`
  MODIFY `match_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `reports`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `system_users`
  MODIFY `sys_usr_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `teams`
  MODIFY `team_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `tournaments`
  MODIFY `tournament_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

ALTER TABLE `contestant-tournament`
  ADD CONSTRAINT `contestant-tournament_ibfk_1` FOREIGN KEY (`contestant_id`) REFERENCES `contestants` (`contestant_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `contestant-tournament_ibfk_2` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`tournament_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `contestant-tournament_ibfk_3` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`) ON DELETE CASCADE;

ALTER TABLE `contestants`
  ADD CONSTRAINT `contestants_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`);

ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`sideA`) REFERENCES `contestants` (`contestant_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`sideB`) REFERENCES `contestants` (`contestant_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_4` FOREIGN KEY (`arena_id`) REFERENCES `arenas` (`arena_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_5` FOREIGN KEY (`tournament`) REFERENCES `tournaments` (`tournament_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_6` FOREIGN KEY (`teamA`) REFERENCES `teams` (`team_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `matches_ibfk_7` FOREIGN KEY (`teamB`) REFERENCES `teams` (`team_id`) ON DELETE CASCADE;


ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`tournament_id`) ON DELETE CASCADE;

ALTER TABLE `teams`
  ADD CONSTRAINT `teams_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `contestants` (`contestant_id`) ON DELETE CASCADE;

ALTER TABLE `tournaments`
  ADD CONSTRAINT `tournaments_ibfk_2` FOREIGN KEY (`operator`) REFERENCES `system_users` (`sys_usr_id`) ON DELETE CASCADE;
  
  INSERT INTO `system_users` (`sys_usr_id`, `login`, `pw_hash`, `permissions`) VALUES
(1, 'admin', '398f2344cda28005d28ac5f7799ff3b257894e971e9fdb700b954906332131dc', 'FULL'),
(2, 'organizator', 'aa3a2087b06587ddec0e3f4769b2380b5d477b4d1e79966ee55b243adb044f3c', 'ORGANIZER');
  
  
COMMIT;
