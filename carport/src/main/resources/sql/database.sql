CREATE TABLE IF NOT EXISTS public.material_category
(
    material_category_id serial NOT NULL,
    material_category_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT material_category_pkey PRIMARY KEY (material_category_id)
    );

CREATE TABLE IF NOT EXISTS public.material_product
(
    material_product_id serial NOT NULL,
    material_category_id integer NOT NULL,
    unit_id integer NOT NULL,
    material_product_name character varying COLLATE pg_catalog."default" NOT NULL,
    material_product_description text COLLATE pg_catalog."default" NOT NULL,
    material_price numeric(10, 2) NOT NULL,
    CONSTRAINT material_product_pkey PRIMARY KEY (material_product_id)
    );

CREATE TABLE IF NOT EXISTS public.order_material
(
    order_material_id serial NOT NULL,
    user_order_id integer NOT NULL,
    material_product_id integer NOT NULL,
    length_cm integer,
    quantity integer NOT NULL,
    note text COLLATE pg_catalog."default",
    total_price numeric(10, 2) NOT NULL,
    CONSTRAINT order_material_pkey PRIMARY KEY (order_material_id)
    );

CREATE TABLE IF NOT EXISTS public.roof_type
(
    roof_type_id serial NOT NULL,
    roof_type_name character varying COLLATE pg_catalog."default" NOT NULL,
    roof_type_deg integer NOT NULL,
    roof_type_price numeric(10, 2) NOT NULL,
    CONSTRAINT roof_type_pkey PRIMARY KEY (roof_type_id)
    );

CREATE TABLE IF NOT EXISTS public.unit
(
    unit_id serial NOT NULL,
    unit_name character varying COLLATE pg_catalog."default" NOT NULL,
    unit_short_name character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT unit_pkey PRIMARY KEY (unit_id)
    );

CREATE TABLE IF NOT EXISTS public."user"
(
    user_email character varying COLLATE pg_catalog."default" NOT NULL,
    user_firstname character varying COLLATE pg_catalog."default" NOT NULL,
    user_lastname character varying COLLATE pg_catalog."default" NOT NULL,
    user_adress character varying COLLATE pg_catalog."default" NOT NULL,
    user_postal_code integer NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (user_email)
    );

CREATE TABLE IF NOT EXISTS public.user_order
(
    user_order_id serial NOT NULL,
    user_email character varying COLLATE pg_catalog."default" NOT NULL,
    order_status character varying NOT NULL,
    roof_type_id integer NOT NULL,
    length_mm integer NOT NULL,
    width_mm integer NOT NULL,
    shed_id integer,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    CONSTRAINT user_order_pkey PRIMARY KEY (user_order_id)
    );

CREATE TABLE IF NOT EXISTS public.shed
(
    shed_id serial NOT NULL,
    shed_width_mm integer NOT NULL,
    shed_length_mm integer NOT NULL,
    CONSTRAINT shed_pkey PRIMARY KEY (shed_id)
    );

ALTER TABLE IF EXISTS public.material_product
    ADD CONSTRAINT material_product_material_category_id_fkey FOREIGN KEY (material_category_id)
    REFERENCES public.material_category (material_category_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.material_product
    ADD CONSTRAINT material_product_unit_id_fkey FOREIGN KEY (unit_id)
    REFERENCES public.unit (unit_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.order_material
    ADD CONSTRAINT order_material_material_product_id_fkey FOREIGN KEY (material_product_id)
    REFERENCES public.material_product (material_product_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.order_material
    ADD CONSTRAINT order_material_user_order_id_fkey FOREIGN KEY (user_order_id)
    REFERENCES public.user_order (user_order_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.user_order
    ADD CONSTRAINT user_order_roof_type_id_fkey FOREIGN KEY (roof_type_id)
    REFERENCES public.roof_type (roof_type_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.user_order
    ADD CONSTRAINT user_order_user_email_fkey FOREIGN KEY (user_email)
    REFERENCES public."user" (user_email) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;


ALTER TABLE IF EXISTS public.user_order
    ADD CONSTRAINT user_order_shed_id_fkey FOREIGN KEY (shed_id)
    REFERENCES public.shed (shed_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;