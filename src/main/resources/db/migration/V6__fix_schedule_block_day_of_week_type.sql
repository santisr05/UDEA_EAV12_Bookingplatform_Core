-- ScheduleBlock.dayOfWeek es un int de Java; Hibernate lo espera como
-- INTEGER por defecto. V3 lo creo como TINYINT (mas angosto), lo cual es
-- logicamente correcto (el valor va de 0 a 6) pero no coincide con el tipo
-- que Hibernate valida. Se ensancha la columna sin tocar los datos.
ALTER TABLE schedule_block MODIFY COLUMN day_of_week INT NOT NULL;