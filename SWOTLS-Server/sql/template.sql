CREATE TABLE `arenas` (`arena_id` int(11) NOT NULL,`name` varchar(50) COLLATE utf16_polish_ci NOT NULL,`location` varchar(50) COLLATE utf16_polish_ci NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `contestants` (`contestant_id` int(11) NOT NULL,`name` varchar(50) COLLATE utf16_polish_ci NOT NULL,`surname` varchar(50) COLLATE utf16_polish_ci NOT NULL,`nickname` varchar(50) COLLATE utf16_polish_ci NOT NULL,`score` int(11) NOT NULL,`language` varchar(50) COLLATE utf16_polish_ci NOT NULL,`contact_info` text COLLATE utf16_polish_ci NOT NULL,`additional_info` text COLLATE utf16_polish_ci,`team_id` int(11) DEFAULT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `matches` (`match_id` int(11) NOT NULL,`sideA` int(11) NOT NULL, `sideB` int(11) NOT NULL,`sideA_score` int(11) DEFAULT NULL,`sideB_score` int(11) DEFAULT NULL,`arena_id` int(11) NOT NULL,`time` datetime NOT NULL,`tournament` int(11) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
CREATE TABLE `system_users` (
  `sys_usr_id` int(11) NOT NULL,
  `login` varchar(20) COLLATE utf16_polish_ci NOT NULL,
  `pw_hash` varchar(32) NOT NULL,
  `permissions` varchar(5) COLLATE utf16_polish_ci DEFAULT NULL
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
  `additional_info` text COLLATE utf16_polish_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;
ALTER TABLE `arenas`
  ADD PRIMARY KEY (`arena_id`);
ALTER TABLE `contestants`
  ADD PRIMARY KEY (`contestant_id`),
  ADD KEY `team_id` (`team_id`);
ALTER TABLE `matches`
  ADD PRIMARY KEY (`match_id`),
  ADD KEY `sideA` (`sideA`),
  ADD KEY `arena_id` (`arena_id`),
  ADD KEY `sideB` (`sideB`),
  ADD KEY `tournament` (`tournament`);
ALTER TABLE `system_users`
  ADD PRIMARY KEY (`sys_usr_id`);
ALTER TABLE `teams`
  ADD PRIMARY KEY (`team_id`),
  ADD KEY `leader_id` (`leader_id`);
ALTER TABLE `tournaments`
  ADD PRIMARY KEY (`tournament_id`),
  ADD KEY `operator` (`operator`);
ALTER TABLE `contestants`
  MODIFY `contestant_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `arenas`
  MODIFY `arena_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `tournaments`
  MODIFY `tournament_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `system_users`
  MODIFY `sys_usr_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `matches`
  MODIFY `match_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `teams`
  MODIFY `team_id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `contestants`
  ADD CONSTRAINT `contestants_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`);
ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`sideA`) REFERENCES `teams` (`team_id`),
  ADD CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`sideB`) REFERENCES `teams` (`team_id`),
   ADD CONSTRAINT `matches_ibfk_4` FOREIGN KEY (`arena_id`) REFERENCES `arenas` (`arena_id`),
  ADD CONSTRAINT `matches_ibfk_3` FOREIGN KEY (`tournament`) REFERENCES `tournaments` (`tournament_id`);
ALTER TABLE `teams`
  ADD CONSTRAINT `teams_ibfk_1` FOREIGN KEY (`leader_id`) REFERENCES `contestants` (`contestant_id`);

ALTER TABLE `tournaments`
  ADD CONSTRAINT `tournaments_ibfk_2` FOREIGN KEY (`operator`) REFERENCES `system_users` (`sys_usr_id`);