INSERT INTO users VALUES(1, 1, '2021-04-21', "Ruslan", "rakbashev25@gmail.com", "password", null, null),
(2, 0, '2021-04-22', "root", "ruslan_15-0510@mail.ru", "password", null, null)
GO

INSERT INTO posts VALUES(1, 1, "NEW", 1, 1, '2021-04-22', "post_1", "about new post", 0),
(2, 0, "NEW", 0, 2, '2021-04-22', "post_2", "2 about new post", 1)
GO

INSERT INTO post_votes VALUES(1, 1, 1, '2021-04-22', 1),
(2, 2, 2, '2021-04-23', 1)
GO

INSERT INTO tags VALUES(1, "#tag_1"),
(2, "#New_tag")
GO

INSERT INTO tag2post VALUES(1, 1, 1),
(2, 2, 2)
GO

INSERT INTO post_comments VALUES(1, null, 1, 1, '2021-04-22', "comment"),
(2, null, 2, 2, '2021-04-22', "comment new post")
GO