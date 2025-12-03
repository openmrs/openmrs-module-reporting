select      t.patient_id, p.gender, p.birthdate, pa.value as birthplace
from        patient t
inner join  person p on t.patient_id = p.person_id
left join   person_attribute pa on p.person_id = pa.person_id
where       pa.person_attribute_type_id = @birthplace
order by    t.patient_id asc
;
