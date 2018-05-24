INSERT INTO weight(weight) VALUES (4);
INSERT INTO weight(weight) VALUES (8);
INSERT INTO weight(weight) VALUES (12);
INSERT INTO weight(weight) VALUES (16);
INSERT INTO weight(weight) VALUES (20);
INSERT INTO weight(weight) VALUES (24);
INSERT INTO weight(weight) VALUES (28);
INSERT INTO weight(weight) VALUES (32);
INSERT INTO weight(weight) VALUES (36);

INSERT INTO exercise_type(name) VALUES ('Swings');
INSERT INTO exercise_type(name) VALUES ('TGU');
INSERT INTO exercise_type(name) VALUES ('Snatch');
INSERT INTO exercise_type(name) VALUES ('One-hand swings');
INSERT INTO exercise_type(name) VALUES ('Drop downs');

INSERT INTO workout(date) VALUES ('2018-05-14');
INSERT INTO workout(date) VALUES ('2018-05-21');

INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (5, 10, 24, 1, 1);
INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (5, 10, 12, 2, 1);
INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (8, 5, 28, 3, 1);

INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (10, 8, 8, 4, 2);
INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (5, 10, 12, 5, 2);
INSERT INTO exercise(series, repetitions, weight, type, workout) VALUES (8, 5, 28, 1, 2);