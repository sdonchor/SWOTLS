package application;

public class DatabaseTemplate {
	private static String sql="-- phpMyAdmin SQL Dump\r\n" + 
			"-- version 4.7.7\r\n" + 
			"-- https://www.phpmyadmin.net/\r\n" + 
			"--\r\n" + 
			"-- Host: sdonchor.nazwa.pl:3306\r\n" + 
			"-- Czas generowania: 19 Lis 2018, 16:33\r\n" + 
			"-- Wersja serwera: 10.1.30-MariaDB\r\n" + 
			"-- Wersja PHP: 5.5.9-1ubuntu4.26\r\n" + 
			"\r\n" + 
			"SET SQL_MODE = \"NO_AUTO_VALUE_ON_ZERO\";\r\n" + 
			"SET AUTOCOMMIT = 0;\r\n" + 
			"START TRANSACTION;\r\n" + 
			"SET time_zone = \"+00:00\";\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Baza danych: `sdonchor_SWOTLS-DB`\r\n" + 
			"--\r\n" + 
			"\r\n" + 
			"-- --------------------------------------------------------\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Struktura tabeli dla tabeli `contestants`\r\n" + 
			"--\r\n" + 
			"\r\n" + 
			"CREATE TABLE `contestants` (\r\n" + 
			"  `contestant_id` int(11) NOT NULL,\r\n" + 
			"  `name` varchar(50) COLLATE utf16_polish_ci NOT NULL,\r\n" + 
			"  `surname` varchar(50) COLLATE utf16_polish_ci NOT NULL,\r\n" + 
			"  `nickname` varchar(50) COLLATE utf16_polish_ci NOT NULL,\r\n" + 
			"  `score` int(11) NOT NULL,\r\n" + 
			"  `language` varchar(50) COLLATE utf16_polish_ci NOT NULL,\r\n" + 
			"  `contact_info` text COLLATE utf16_polish_ci NOT NULL,\r\n" + 
			"  `additional_info` text COLLATE utf16_polish_ci,\r\n" + 
			"  `team_id` int(11) DEFAULT NULL\r\n" + 
			") ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_polish_ci;\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Indeksy dla zrzutów tabel\r\n" + 
			"--\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Indeksy dla tabeli `contestants`\r\n" + 
			"--\r\n" + 
			"ALTER TABLE `contestants`\r\n" + 
			"  ADD PRIMARY KEY (`contestant_id`),\r\n" + 
			"  ADD KEY `team_id` (`team_id`);\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- AUTO_INCREMENT for dumped tables\r\n" + 
			"--\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- AUTO_INCREMENT dla tabeli `contestants`\r\n" + 
			"--\r\n" + 
			"ALTER TABLE `contestants`\r\n" + 
			"  MODIFY `contestant_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Ograniczenia dla zrzutów tabel\r\n" + 
			"--\r\n" + 
			"\r\n" + 
			"--\r\n" + 
			"-- Ograniczenia dla tabeli `contestants`\r\n" + 
			"--\r\n" + 
			"ALTER TABLE `contestants`\r\n" + 
			"  ADD CONSTRAINT `contestants_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`team_id`);\r\n" + 
			"COMMIT;\r\n";
	public static String GetScript() {
		return sql;
	}
}
