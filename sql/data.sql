
CREATE TABLE exercise
(
	id integer NOT NULL,
	series integer NOT NULL   DEFAULT 1,
	repetitions integer NOT NULL   DEFAULT 1,
	weight real NOT NULL   DEFAULT 1,
	type integer NOT NULL,
	workout integer NOT NULL
)
;


CREATE TABLE exercise_type
(
	id integer NOT NULL,
	name varchar(50) NULL
)
;

CREATE TABLE weights
(
	weight real NOT NULL
)
;

CREATE TABLE workout
(
	id integer NOT NULL,
	date date NOT NULL,
	time time without time zone NULL
)
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE exercise ADD CONSTRAINT PK_exercise
	PRIMARY KEY (id)
;

CREATE INDEX IXFK_exercise_exercise_type ON exercise (type ASC)
;

CREATE INDEX IXFK_exercise_workout ON exercise (workout ASC)
;

ALTER TABLE exercise_type ADD CONSTRAINT PK_exercise_type
	PRIMARY KEY (id)
;

ALTER TABLE weights ADD CONSTRAINT PK_weights
	PRIMARY KEY (weight)
;

ALTER TABLE workout ADD CONSTRAINT PK_workout
	PRIMARY KEY (id)
;

/* Create Foreign Key Constraints */

ALTER TABLE exercise ADD CONSTRAINT FK_exercise_exercise_type
	FOREIGN KEY (type) REFERENCES exercise_type (id) ON DELETE Cascade
;

ALTER TABLE exercise ADD CONSTRAINT FK_exercise_workout
	FOREIGN KEY (workout) REFERENCES workout (id) ON DELETE Cascade
;


