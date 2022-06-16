do $$
begin

create sequence "html_parser".id_gen
    increment 1
    minvalue 1
    no maxvalue
    start with 1;

create table "html_parser".post (
    id int default nextval('"html_parser".id_gen')
    , name text
    , text_ text
    , link text
    , created timestamp
    , constraint i_id_link primary key(id, link)
);

end$$;