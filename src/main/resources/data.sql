-- Fyller tabellen om och endast om den är tom
-- Detta sker även om användaren manuellt raderat all data, men bättre det än
-- att ta bort allt hen lagt till med spring.jpa.hibernate.ddl-auto=create eller
-- create-drop
insert into person(NAME, GENDER, OCCUPATION, CITY, BIRTHDAY, ARCANA)
select *
from ((select 'Eleanor Rigby', 'FEMALE', 'Unemployed', 'Liverpool', '1966-08-05', 'FOOL')
      union all
      (select 'Father McKenzie', 'MALE', 'Pastor', 'Liverpool', '1966-08-05', 'HIEROPHANT')
      union all
      (select 'John Lennon', 'MALE', 'Vocalist/Guitarist', 'Liverpool again', '1940-10-09', 'MAGICIAN')
      union all
      (select 'Paul McCartney', 'MALE', 'Vocalist/Bassist', 'Still Liverpool', '1942-06-18', 'FOOL')
      union all
      (select 'George Harrison', 'MALE', 'Guitarist', 'Liverpool yet again', '1943-02-25', 'HERMIT')
      union all
      (select 'Ringo Starr', 'MALE', 'Drummer', 'You got it, it''s Liverpool', '1940-07-07', 'STAR'))
where not exists(select id from person);