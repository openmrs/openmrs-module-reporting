select  t.patient_id, p.gender, p.birthdate
from    patient t, person p
where   t.patient_id = p.person_id
and     t.patient_id = 2;
