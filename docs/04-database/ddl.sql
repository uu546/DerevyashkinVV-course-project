CREATE TABLE public.categories (
    id integer NOT NULL,
    title character varying(100) NOT NULL
);

COMMENT ON TABLE public.categories IS 'Категории товаров';

CREATE SEQUENCE public.categories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;

CREATE TABLE public.inventories (
    id integer NOT NULL,
    product_id integer NOT NULL,
    location_id integer NOT NULL,
    quantity integer DEFAULT 0,
    CONSTRAINT inventories_quantity_check CHECK (quantity >= 0)
);

COMMENT ON TABLE public.inventories IS 'Текущие остатки товаров на локациях';

CREATE SEQUENCE public.inventories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.inventories_id_seq OWNED BY public.inventories.id;

CREATE TABLE public.locations (
    id integer NOT NULL,
    warehouse_id integer NOT NULL,
    name character varying(50) NOT NULL,
    type_id integer NOT NULL,
    temperature_id integer
);

COMMENT ON TABLE public.locations IS 'Места хранения на складах';

CREATE SEQUENCE public.locations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.locations_id_seq OWNED BY public.locations.id;

CREATE TABLE public.movements (
    id integer NOT NULL,
    product_id integer NOT NULL,
    from_location_id integer,
    to_location_id integer,
    quantity integer NOT NULL,
    movement_date date DEFAULT now(),
    CONSTRAINT chk_movements_locations_diff CHECK (
        from_location_id <> to_location_id
        OR from_location_id IS NULL
        OR to_location_id IS NULL
    ),
    CONSTRAINT movements_quantity_check CHECK (quantity > 0)
);

COMMENT ON TABLE public.movements IS 'История перемещений товаров между локациями';

CREATE SEQUENCE public.movements_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.movements_id_seq OWNED BY public.movements.id;

CREATE TABLE public.products (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    is_perishable boolean,
    expiry_days integer,
    category_id integer NOT NULL,
    unit_id integer NOT NULL
);

COMMENT ON TABLE public.products IS 'Номенклатура товаров';

CREATE SEQUENCE public.products_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.products_id_seq OWNED BY public.products.id;

CREATE TABLE public.temperatures (
    id integer NOT NULL,
    title character varying(50) NOT NULL
);

COMMENT ON TABLE public.temperatures IS 'Температурные режимы хранения';

CREATE SEQUENCE public.temperatures_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.temperatures_id_seq OWNED BY public.temperatures.id;

CREATE TABLE public.types (
    id integer NOT NULL,
    title character varying(50) NOT NULL
);

COMMENT ON TABLE public.types IS 'Типы локаций (стеллаж, паллета, холодильник)';

CREATE SEQUENCE public.types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.types_id_seq OWNED BY public.types.id;

CREATE TABLE public.units (
    id integer NOT NULL,
    title character varying(50) NOT NULL
);

CREATE SEQUENCE public.units_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.units_id_seq OWNED BY public.units.id;

CREATE TABLE public.warehouses (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    address character varying(255)
);

COMMENT ON TABLE public.warehouses IS 'Склады';

CREATE SEQUENCE public.warehouses_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    CACHE 1;

ALTER SEQUENCE public.warehouses_id_seq OWNED BY public.warehouses.id;

-- DEFAULTS

ALTER TABLE ONLY public.categories
    ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);

ALTER TABLE ONLY public.inventories
    ALTER COLUMN id SET DEFAULT nextval('public.inventories_id_seq'::regclass);

ALTER TABLE ONLY public.locations
    ALTER COLUMN id SET DEFAULT nextval('public.locations_id_seq'::regclass);

ALTER TABLE ONLY public.movements
    ALTER COLUMN id SET DEFAULT nextval('public.movements_id_seq'::regclass);

ALTER TABLE ONLY public.products
    ALTER COLUMN id SET DEFAULT nextval('public.products_id_seq'::regclass);

ALTER TABLE ONLY public.temperatures
    ALTER COLUMN id SET DEFAULT nextval('public.temperatures_id_seq'::regclass);

ALTER TABLE ONLY public.types
    ALTER COLUMN id SET DEFAULT nextval('public.types_id_seq'::regclass);

ALTER TABLE ONLY public.units
    ALTER COLUMN id SET DEFAULT nextval('public.units_id_seq'::regclass);

ALTER TABLE ONLY public.warehouses
    ALTER COLUMN id SET DEFAULT nextval('public.warehouses_id_seq'::regclass);

-- PRIMARY KEYS

ALTER TABLE ONLY public.categories ADD CONSTRAINT categories_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.inventories ADD CONSTRAINT inventories_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.locations ADD CONSTRAINT locations_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.movements ADD CONSTRAINT movements_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.products ADD CONSTRAINT products_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.temperatures ADD CONSTRAINT temperatures_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.types ADD CONSTRAINT types_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.units ADD CONSTRAINT units_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.warehouses ADD CONSTRAINT warehouses_pkey PRIMARY KEY (id);

-- UNIQUE

ALTER TABLE ONLY public.inventories
    ADD CONSTRAINT uq_inventories_product_location UNIQUE (product_id, location_id);

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT uq_locations_warehouse_name_type UNIQUE (warehouse_id, name, type_id);

ALTER TABLE ONLY public.units
    ADD CONSTRAINT uq_units_title UNIQUE (title);

-- INDEXES

CREATE INDEX idx_inventories_location_id ON public.inventories (location_id);
CREATE INDEX idx_inventories_product_id ON public.inventories (product_id);

CREATE INDEX idx_locations_name ON public.locations (name);
CREATE INDEX idx_locations_temperature_id ON public.locations (temperature_id);
CREATE INDEX idx_locations_type_id ON public.locations (type_id);
CREATE INDEX idx_locations_warehouse_id ON public.locations (warehouse_id);

CREATE INDEX idx_movements_from_location_id ON public.movements (from_location_id);
CREATE INDEX idx_movements_to_location_id ON public.movements (to_location_id);
CREATE INDEX idx_movements_product_id ON public.movements (product_id);
CREATE INDEX idx_movements_movement_date ON public.movements (movement_date);

CREATE INDEX idx_products_category_id ON public.products (category_id);
CREATE INDEX idx_products_is_perishable ON public.products (is_perishable);
CREATE INDEX idx_products_name ON public.products (name);

-- FOREIGN KEYS

ALTER TABLE ONLY public.inventories
    ADD CONSTRAINT fk_inventories_location FOREIGN KEY (location_id) REFERENCES public.locations(id);

ALTER TABLE ONLY public.inventories
    ADD CONSTRAINT fk_inventories_product FOREIGN KEY (product_id) REFERENCES public.products(id);

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT fk_locations_temperature FOREIGN KEY (temperature_id) REFERENCES public.temperatures(id);

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT fk_locations_type FOREIGN KEY (type_id) REFERENCES public.types(id);

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT fk_locations_warehouse FOREIGN KEY (warehouse_id) REFERENCES public.warehouses(id);

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT fk_movements_from_location FOREIGN KEY (from_location_id) REFERENCES public.locations(id);

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT fk_movements_to_location FOREIGN KEY (to_location_id) REFERENCES public.locations(id);

ALTER TABLE ONLY public.movements
    ADD CONSTRAINT fk_movements_product FOREIGN KEY (product_id) REFERENCES public.products(id);

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES public.categories(id);

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_unit FOREIGN KEY (unit_id) REFERENCES public.units(id);