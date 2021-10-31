Create Table `users` (
`id` INT NOT NULL AUTO_INCREMENT,
`is_moderator` TINYINT NOT NULL,
`reg_time` DATETIME NOT NULL,
`name` VARCHAR(255)NOT NULL,
`email` VARCHAR(255)NOT NULL,
`password` VARCHAR(255)NOT NULL,
`code` VARCHAR(255),
`photo` TEXT,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `posts` (
`id` INT NOT NULL AUTO_INCREMENT,
`is_active` TINYINT NOT NULL,
`moderation_status` ENUM('NEW', 'ACCEPTED', 'DECLINED') NOT NULL,
`moderator_id` INT,
`user_id` INT NOT NULL,
`time` DATETIME NOT NULL,
`title` VARCHAR(255) NOT NULL,
`text` TEXT NOT NULL,
`view_count` INT NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `post_votes` (
`id` INT NOT NULL AUTO_INCREMENT,
`user_id` INT NOT NULL,
`post_id` INT NOT NULL,
`time` DATETIME NOT NULL,
`value` TINYINT NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `tags` (
`id` INT NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `tag2post` (
`id` INT NOT NULL AUTO_INCREMENT,
`post_id` INT NOT NULL,
`tag_id` INT NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `post_comments` (
`id` INT NOT NULL AUTO_INCREMENT,
`parent_id` INT,
`post_id` INT NOT NULL,
`user_id` INT NOT NULL,
`time` DATETIME NOT NULL,
`text` TEXT NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `captcha_codes` (
`id` INT NOT NULL AUTO_INCREMENT,
`time` DATETIME NOT NULL,
`code` TINYTEXT NOT NULL,
`secret_code` TINYTEXT NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

Create Table `global_settings` (
`id` INT NOT NULL AUTO_INCREMENT,
`code` VARCHAR(255) NOT NULL,
`name` VARCHAR(255) NOT NULL,
`value` VARCHAR(255) NOT NULL,
PRIMARY KEY(id))
--ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
GO

INSERT INTO `global_settings` (code, name, value) VALUES
('MULTIUSER_MODE', 'Многопользовательский режим', 'YES'),
('POST_PREMODERATION', 'Премодерация постов', 'YES'),
('STATISTICS_IS_PUBLIC', 'Показывать всем статистику блога', 'YES')
GO