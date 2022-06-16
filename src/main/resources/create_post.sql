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
    , constraint pk_post_id primary key(id)
    , constraint u_post_link unique(link)
);

end$$;