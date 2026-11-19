--
-- PostgreSQL database dump
--


-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-05-20 17:00:19

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA public;


--
-- TOC entry 5202 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 239 (class 1259 OID 16828)
-- Name: abwesenheit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.abwesenheit (
    id bigint NOT NULL,
    aktiv boolean NOT NULL,
    bemerkung character varying(255),
    bis date NOT NULL,
    grund character varying(255),
    von date NOT NULL,
    lehrkraft_id bigint NOT NULL,
    CONSTRAINT abwesenheit_grund_check CHECK (((grund)::text = ANY ((ARRAY['KRANK'::character varying, 'FORTBILDUNG'::character varying, 'EXKURSION'::character varying, 'SONSTIGES'::character varying])::text[])))
);


--
-- TOC entry 238 (class 1259 OID 16827)
-- Name: abwesenheit_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.abwesenheit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5203 (class 0 OID 0)
-- Dependencies: 238
-- Name: abwesenheit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.abwesenheit_id_seq OWNED BY public.abwesenheit.id;


--
-- TOC entry 240 (class 1259 OID 16842)
-- Name: admin_benutzer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admin_benutzer (
    id bigint NOT NULL
);


--
-- TOC entry 242 (class 1259 OID 16849)
-- Name: benutzer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.benutzer (
    id bigint NOT NULL,
    aktiv boolean NOT NULL,
    benutzername character varying(255) NOT NULL,
    passworthash character varying(255) NOT NULL,
    rolle character varying(255) NOT NULL,
    CONSTRAINT benutzer_rolle_check CHECK (((rolle)::text = ANY ((ARRAY['ADMINISTRATOR'::character varying, 'LEHRER'::character varying, 'SCHUELER'::character varying])::text[])))
);


--
-- TOC entry 241 (class 1259 OID 16848)
-- Name: benutzer_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.benutzer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5204 (class 0 OID 0)
-- Dependencies: 241
-- Name: benutzer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.benutzer_id_seq OWNED BY public.benutzer.id;


--
-- TOC entry 250 (class 1259 OID 16994)
-- Name: erziehungsberechtigter; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.erziehungsberechtigter (
    id bigint NOT NULL,
    adresse character varying(255),
    beziehung character varying(255) NOT NULL,
    email character varying(255),
    nachname character varying(255) NOT NULL,
    telefon character varying(255),
    vorname character varying(255) NOT NULL,
    schueler_id bigint NOT NULL,
    CONSTRAINT erziehungsberechtigter_beziehung_check CHECK (((beziehung)::text = ANY ((ARRAY['MUTTER'::character varying, 'VATER'::character varying, 'VORMUND'::character varying, 'SONSTIGE'::character varying])::text[])))
);


--
-- TOC entry 249 (class 1259 OID 16993)
-- Name: erziehungsberechtigter_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.erziehungsberechtigter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5205 (class 0 OID 0)
-- Dependencies: 249
-- Name: erziehungsberechtigter_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.erziehungsberechtigter_id_seq OWNED BY public.erziehungsberechtigter.id;


--
-- TOC entry 220 (class 1259 OID 16672)
-- Name: fach; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fach (
    id bigint NOT NULL,
    bezeichnung character varying(255),
    kuerzel character varying(255),
    wochenstundenproklasse integer NOT NULL
);


--
-- TOC entry 219 (class 1259 OID 16671)
-- Name: fach_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.fach_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5206 (class 0 OID 0)
-- Dependencies: 219
-- Name: fach_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.fach_id_seq OWNED BY public.fach.id;


--
-- TOC entry 222 (class 1259 OID 16683)
-- Name: klasse; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.klasse (
    id bigint NOT NULL,
    bezeichnung character varying(255),
    jahrgangsstufe integer NOT NULL,
    schueleranzahl integer NOT NULL,
    lehrkraft_id bigint
);


--
-- TOC entry 221 (class 1259 OID 16682)
-- Name: klasse_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.klasse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5207 (class 0 OID 0)
-- Dependencies: 221
-- Name: klasse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.klasse_id_seq OWNED BY public.klasse.id;


--
-- TOC entry 243 (class 1259 OID 16863)
-- Name: lehrer_benutzer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lehrer_benutzer (
    id bigint NOT NULL,
    lehrkraft_id bigint NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 16693)
-- Name: lehrkraft; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lehrkraft (
    id bigint NOT NULL,
    kuerzel character varying(255),
    name character varying(255),
    sollstunden integer NOT NULL,
    sperrzeiten text
);


--
-- TOC entry 235 (class 1259 OID 16788)
-- Name: lehrkraft_fach; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lehrkraft_fach (
    lehrkraft_id bigint NOT NULL,
    fach_id bigint NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 16692)
-- Name: lehrkraft_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lehrkraft_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5208 (class 0 OID 0)
-- Dependencies: 223
-- Name: lehrkraft_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.lehrkraft_id_seq OWNED BY public.lehrkraft.id;


--
-- TOC entry 226 (class 1259 OID 16704)
-- Name: raum; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.raum (
    id bigint NOT NULL,
    bezeichnung character varying(255),
    kapazitaet integer NOT NULL,
    raumtyp character varying(255),
    CONSTRAINT raum_raumtyp_check CHECK (((raumtyp)::text = ANY ((ARRAY['STANDARD'::character varying, 'FACHRAUM_CHEMIE'::character varying, 'MUSIK'::character varying, 'SPORTHALLE'::character varying, 'INFORMATIK'::character varying])::text[])))
);


--
-- TOC entry 225 (class 1259 OID 16703)
-- Name: raum_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.raum_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5209 (class 0 OID 0)
-- Dependencies: 225
-- Name: raum_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.raum_id_seq OWNED BY public.raum.id;


--
-- TOC entry 248 (class 1259 OID 16975)
-- Name: schueler; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schueler (
    id bigint NOT NULL,
    geburtsdatum date,
    nachname character varying(255) NOT NULL,
    status character varying(255),
    vorname character varying(255) NOT NULL,
    klasse_id bigint NOT NULL,
    CONSTRAINT schueler_status_check CHECK (((status)::text = ANY ((ARRAY['AKTIV'::character varying, 'ABGEGANGEN'::character varying])::text[])))
);


--
-- TOC entry 244 (class 1259 OID 16870)
-- Name: schueler_benutzer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schueler_benutzer (
    id bigint NOT NULL,
    klasse_id bigint NOT NULL
);


--
-- TOC entry 247 (class 1259 OID 16974)
-- Name: schueler_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.schueler_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5210 (class 0 OID 0)
-- Dependencies: 247
-- Name: schueler_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.schueler_id_seq OWNED BY public.schueler.id;


--
-- TOC entry 228 (class 1259 OID 16716)
-- Name: schuljahr; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.schuljahr (
    id bigint NOT NULL,
    bezeichnung character varying(255),
    end_datum date,
    istaktiv boolean NOT NULL,
    start_datum date
);


--
-- TOC entry 227 (class 1259 OID 16715)
-- Name: schuljahr_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.schuljahr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5211 (class 0 OID 0)
-- Dependencies: 227
-- Name: schuljahr_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.schuljahr_id_seq OWNED BY public.schuljahr.id;


--
-- TOC entry 237 (class 1259 OID 16794)
-- Name: sperrzeit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sperrzeit (
    id bigint NOT NULL,
    lehrkraft_id bigint NOT NULL,
    zeitslot_id bigint NOT NULL
);


--
-- TOC entry 236 (class 1259 OID 16793)
-- Name: sperrzeit_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.sperrzeit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5212 (class 0 OID 0)
-- Dependencies: 236
-- Name: sperrzeit_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.sperrzeit_id_seq OWNED BY public.sperrzeit.id;


--
-- TOC entry 230 (class 1259 OID 16725)
-- Name: stunde; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stunde (
    id bigint NOT NULL,
    geaendert boolean NOT NULL,
    istausfall boolean NOT NULL,
    istvertretung boolean NOT NULL,
    fach_id bigint,
    klasse_id bigint,
    lehrkraft_id bigint,
    raum_id bigint,
    stundenplan_id bigint,
    zeitslot_id bigint
);


--
-- TOC entry 229 (class 1259 OID 16724)
-- Name: stunde_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stunde_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5213 (class 0 OID 0)
-- Dependencies: 229
-- Name: stunde_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stunde_id_seq OWNED BY public.stunde.id;


--
-- TOC entry 232 (class 1259 OID 16736)
-- Name: stundenplan; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.stundenplan (
    id bigint NOT NULL,
    gueltigab date,
    schuljahr_id bigint NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 16735)
-- Name: stundenplan_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.stundenplan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5214 (class 0 OID 0)
-- Dependencies: 231
-- Name: stundenplan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.stundenplan_id_seq OWNED BY public.stundenplan.id;


--
-- TOC entry 246 (class 1259 OID 16879)
-- Name: vertretung; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.vertretung (
    id bigint NOT NULL,
    bemerkung character varying(255),
    datum date NOT NULL,
    grund character varying(255),
    stunde_id bigint NOT NULL,
    vertretungslehrer_id bigint,
    CONSTRAINT vertretung_grund_check CHECK (((grund)::text = ANY ((ARRAY['KRANK'::character varying, 'FORTBILDUNG'::character varying, 'EXKURSION'::character varying, 'SONSTIGES'::character varying])::text[])))
);


--
-- TOC entry 245 (class 1259 OID 16878)
-- Name: vertretung_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.vertretung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5215 (class 0 OID 0)
-- Dependencies: 245
-- Name: vertretung_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.vertretung_id_seq OWNED BY public.vertretung.id;


--
-- TOC entry 234 (class 1259 OID 16744)
-- Name: zeitslot; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.zeitslot (
    id bigint NOT NULL,
    endzeit time(6) without time zone,
    startzeit time(6) without time zone,
    stundennummer integer NOT NULL,
    wochentag character varying(255),
    CONSTRAINT zeitslot_wochentag_check CHECK (((wochentag)::text = ANY ((ARRAY['MONTAG'::character varying, 'DIENSTAG'::character varying, 'MITTWOCH'::character varying, 'DONNERSTAG'::character varying, 'FREITAG'::character varying])::text[])))
);


--
-- TOC entry 233 (class 1259 OID 16743)
-- Name: zeitslot_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.zeitslot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5216 (class 0 OID 0)
-- Dependencies: 233
-- Name: zeitslot_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.zeitslot_id_seq OWNED BY public.zeitslot.id;


--
-- TOC entry 4946 (class 2604 OID 16831)
-- Name: abwesenheit id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.abwesenheit ALTER COLUMN id SET DEFAULT nextval('public.abwesenheit_id_seq'::regclass);


--
-- TOC entry 4947 (class 2604 OID 16852)
-- Name: benutzer id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.benutzer ALTER COLUMN id SET DEFAULT nextval('public.benutzer_id_seq'::regclass);


--
-- TOC entry 4950 (class 2604 OID 16997)
-- Name: erziehungsberechtigter id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.erziehungsberechtigter ALTER COLUMN id SET DEFAULT nextval('public.erziehungsberechtigter_id_seq'::regclass);


--
-- TOC entry 4937 (class 2604 OID 16675)
-- Name: fach id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fach ALTER COLUMN id SET DEFAULT nextval('public.fach_id_seq'::regclass);


--
-- TOC entry 4938 (class 2604 OID 16686)
-- Name: klasse id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.klasse ALTER COLUMN id SET DEFAULT nextval('public.klasse_id_seq'::regclass);


--
-- TOC entry 4939 (class 2604 OID 16696)
-- Name: lehrkraft id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrkraft ALTER COLUMN id SET DEFAULT nextval('public.lehrkraft_id_seq'::regclass);


--
-- TOC entry 4940 (class 2604 OID 16707)
-- Name: raum id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.raum ALTER COLUMN id SET DEFAULT nextval('public.raum_id_seq'::regclass);


--
-- TOC entry 4949 (class 2604 OID 16978)
-- Name: schueler id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler ALTER COLUMN id SET DEFAULT nextval('public.schueler_id_seq'::regclass);


--
-- TOC entry 4941 (class 2604 OID 16719)
-- Name: schuljahr id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schuljahr ALTER COLUMN id SET DEFAULT nextval('public.schuljahr_id_seq'::regclass);


--
-- TOC entry 4945 (class 2604 OID 16797)
-- Name: sperrzeit id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sperrzeit ALTER COLUMN id SET DEFAULT nextval('public.sperrzeit_id_seq'::regclass);


--
-- TOC entry 4942 (class 2604 OID 16728)
-- Name: stunde id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde ALTER COLUMN id SET DEFAULT nextval('public.stunde_id_seq'::regclass);


--
-- TOC entry 4943 (class 2604 OID 16739)
-- Name: stundenplan id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stundenplan ALTER COLUMN id SET DEFAULT nextval('public.stundenplan_id_seq'::regclass);


--
-- TOC entry 4948 (class 2604 OID 16882)
-- Name: vertretung id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vertretung ALTER COLUMN id SET DEFAULT nextval('public.vertretung_id_seq'::regclass);


--
-- TOC entry 4944 (class 2604 OID 16747)
-- Name: zeitslot id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.zeitslot ALTER COLUMN id SET DEFAULT nextval('public.zeitslot_id_seq'::regclass);


--
-- TOC entry 5185 (class 0 OID 16828)
-- Dependencies: 239
-- Data for Name: abwesenheit; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 5186 (class 0 OID 16842)
-- Dependencies: 240
-- Data for Name: admin_benutzer; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.admin_benutzer VALUES (1);


--
-- TOC entry 5188 (class 0 OID 16849)
-- Dependencies: 242
-- Data for Name: benutzer; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.benutzer VALUES (1, true, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMINISTRATOR');
INSERT INTO public.benutzer VALUES (35, true, 'ba', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (36, true, 'kl', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (37, true, 'ah', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (38, true, 'sch', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (39, true, 'schi', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (40, true, 'krz', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (41, true, 'zy', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (42, true, 'fa', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (43, true, 'ho', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (44, true, 'le', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (45, true, 'sf', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (46, true, 'ju', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (47, true, 'bu', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (48, true, 'br', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (49, true, 'ko', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (50, true, 'vo', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (51, true, 'hö', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (52, true, 'me', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (53, true, 're', 'c8b470463d8d14419589a948adac62a0cbf92178dc74f09eae954cf7e34de7ae', 'LEHRER');
INSERT INTO public.benutzer VALUES (54, true, '5b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (55, true, '6a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (56, true, '5a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (57, true, '6b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (58, true, '7a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (59, true, '7b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (60, true, '8a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (61, true, '10a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (62, true, '8b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (63, true, '10b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (64, true, '9a', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');
INSERT INTO public.benutzer VALUES (65, true, '9b', '8d198e4a7ec9af1114ddb11a75891593db474823ffc283e55710c79d26126d36', 'SCHUELER');


--
-- TOC entry 5196 (class 0 OID 16994)
-- Dependencies: 250
-- Data for Name: erziehungsberechtigter; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.erziehungsberechtigter VALUES (2, 'Musterweg 1, 58636 Iserlohn', 'VATER', 'eltern2@example.com', 'Beispiel', '0000 000002', 'Paul', 7);
INSERT INTO public.erziehungsberechtigter VALUES (3, 'Musterweg 2, 58636 Iserlohn', 'VATER', 'eltern3@example.com', 'Muster', '0000 000003', 'Lukas', 3);
INSERT INTO public.erziehungsberechtigter VALUES (4, 'Musterweg 3, 58636 Iserlohn', 'MUTTER', 'eltern4@example.com', 'Lang', '0000 000004', 'Anna', 8);
INSERT INTO public.erziehungsberechtigter VALUES (5, 'Musterweg 4, 58636 Iserlohn', 'VATER', 'eltern5@example.com', 'Roth', '0000 000005', 'Peter', 5);
INSERT INTO public.erziehungsberechtigter VALUES (6, 'Musterweg 5, 58636 Iserlohn', 'MUTTER', 'eltern6@example.com', 'Beispiel', '0000 000006', 'Lisa', 7);
INSERT INTO public.erziehungsberechtigter VALUES (7, 'Musterweg 4, 58636 Iserlohn', 'MUTTER', 'eltern7@example.com', 'Roth', '0000 000007', 'Nina', 5);


--
-- TOC entry 5166 (class 0 OID 16672)
-- Dependencies: 220
-- Data for Name: fach; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fach VALUES (1, 'Mathematik', 'Ma', 4);
INSERT INTO public.fach VALUES (2, 'Sport', 'Sp', 2);
INSERT INTO public.fach VALUES (3, 'Deutsch', 'De', 4);
INSERT INTO public.fach VALUES (4, 'Englisch', 'En', 4);
INSERT INTO public.fach VALUES (5, 'Biologie', 'Bio', 2);
INSERT INTO public.fach VALUES (6, 'Geschichte', 'Ge', 2);
INSERT INTO public.fach VALUES (7, 'Politik', 'Po', 2);
INSERT INTO public.fach VALUES (9, 'Chemie', 'Ch', 2);
INSERT INTO public.fach VALUES (10, 'Religion', 'Re', 2);
INSERT INTO public.fach VALUES (11, 'Erdkunde', 'Ek', 2);
INSERT INTO public.fach VALUES (8, 'Musik', 'Mu', 2);


--
-- TOC entry 5168 (class 0 OID 16683)
-- Dependencies: 222
-- Data for Name: klasse; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.klasse VALUES (1, '5b', 5, 0, 5);
INSERT INTO public.klasse VALUES (2, '6a', 6, 0, 6);
INSERT INTO public.klasse VALUES (3, '5a', 5, 0, 4);
INSERT INTO public.klasse VALUES (4, '6b', 6, 0, 7);
INSERT INTO public.klasse VALUES (5, '7a', 7, 0, 10);
INSERT INTO public.klasse VALUES (6, '7b', 7, 0, 12);
INSERT INTO public.klasse VALUES (7, '8a', 8, 0, 8);
INSERT INTO public.klasse VALUES (8, '10a', 10, 0, 11);
INSERT INTO public.klasse VALUES (9, '8b', 8, 0, 9);
INSERT INTO public.klasse VALUES (10, '10b', 10, 0, 23);
INSERT INTO public.klasse VALUES (11, '9a', 9, 0, 24);
INSERT INTO public.klasse VALUES (12, '9b', 9, 0, 25);


--
-- TOC entry 5189 (class 0 OID 16863)
-- Dependencies: 243
-- Data for Name: lehrer_benutzer; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.lehrer_benutzer VALUES (35, 8);
INSERT INTO public.lehrer_benutzer VALUES (36, 6);
INSERT INTO public.lehrer_benutzer VALUES (37, 4);
INSERT INTO public.lehrer_benutzer VALUES (38, 9);
INSERT INTO public.lehrer_benutzer VALUES (39, 24);
INSERT INTO public.lehrer_benutzer VALUES (40, 12);
INSERT INTO public.lehrer_benutzer VALUES (41, 25);
INSERT INTO public.lehrer_benutzer VALUES (42, 7);
INSERT INTO public.lehrer_benutzer VALUES (43, 18);
INSERT INTO public.lehrer_benutzer VALUES (44, 11);
INSERT INTO public.lehrer_benutzer VALUES (45, 19);
INSERT INTO public.lehrer_benutzer VALUES (46, 17);
INSERT INTO public.lehrer_benutzer VALUES (47, 5);
INSERT INTO public.lehrer_benutzer VALUES (48, 22);
INSERT INTO public.lehrer_benutzer VALUES (49, 21);
INSERT INTO public.lehrer_benutzer VALUES (50, 10);
INSERT INTO public.lehrer_benutzer VALUES (51, 16);
INSERT INTO public.lehrer_benutzer VALUES (52, 20);
INSERT INTO public.lehrer_benutzer VALUES (53, 23);


--
-- TOC entry 5170 (class 0 OID 16693)
-- Dependencies: 224
-- Data for Name: lehrkraft; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.lehrkraft VALUES (5, 'Bu', 'Bunte', 20, NULL);
INSERT INTO public.lehrkraft VALUES (10, 'Vo', 'Vogt', 20, NULL);
INSERT INTO public.lehrkraft VALUES (12, 'Krz', 'Kranz', 20, NULL);
INSERT INTO public.lehrkraft VALUES (16, 'Hö', 'Höfer', 20, NULL);
INSERT INTO public.lehrkraft VALUES (7, 'Fa', 'Falk', 20, NULL);
INSERT INTO public.lehrkraft VALUES (17, 'Ju', 'Jansen', 20, NULL);
INSERT INTO public.lehrkraft VALUES (18, 'Ho', 'Horn', 20, NULL);
INSERT INTO public.lehrkraft VALUES (20, 'Me', 'Meier', 20, NULL);
INSERT INTO public.lehrkraft VALUES (22, 'Br', 'Brandt', 20, NULL);
INSERT INTO public.lehrkraft VALUES (23, 'Re', 'Ritter', 20, NULL);
INSERT INTO public.lehrkraft VALUES (24, 'Schi', 'Schirmer', 20, NULL);
INSERT INTO public.lehrkraft VALUES (25, 'Zy', 'Zeller', 20, NULL);
INSERT INTO public.lehrkraft VALUES (8, 'Ba', 'Bach', 25, NULL);
INSERT INTO public.lehrkraft VALUES (6, 'Kl', 'Klein', 25, NULL);
INSERT INTO public.lehrkraft VALUES (4, 'Ah', 'Albers', 25, NULL);
INSERT INTO public.lehrkraft VALUES (11, 'Le', 'Lenz', 25, NULL);
INSERT INTO public.lehrkraft VALUES (19, 'Sf', 'Seifert', 16, NULL);
INSERT INTO public.lehrkraft VALUES (21, 'Ko', 'Kolb', 12, NULL);
INSERT INTO public.lehrkraft VALUES (9, 'Sch', 'Schulz', 20, NULL);


--
-- TOC entry 5181 (class 0 OID 16788)
-- Dependencies: 235
-- Data for Name: lehrkraft_fach; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.lehrkraft_fach VALUES (10, 5);
INSERT INTO public.lehrkraft_fach VALUES (10, 8);
INSERT INTO public.lehrkraft_fach VALUES (5, 3);
INSERT INTO public.lehrkraft_fach VALUES (5, 6);
INSERT INTO public.lehrkraft_fach VALUES (23, 3);
INSERT INTO public.lehrkraft_fach VALUES (23, 7);
INSERT INTO public.lehrkraft_fach VALUES (16, 5);
INSERT INTO public.lehrkraft_fach VALUES (16, 2);
INSERT INTO public.lehrkraft_fach VALUES (7, 11);
INSERT INTO public.lehrkraft_fach VALUES (7, 1);
INSERT INTO public.lehrkraft_fach VALUES (17, 11);
INSERT INTO public.lehrkraft_fach VALUES (17, 4);
INSERT INTO public.lehrkraft_fach VALUES (18, 1);
INSERT INTO public.lehrkraft_fach VALUES (18, 2);
INSERT INTO public.lehrkraft_fach VALUES (20, 11);
INSERT INTO public.lehrkraft_fach VALUES (20, 4);
INSERT INTO public.lehrkraft_fach VALUES (22, 1);
INSERT INTO public.lehrkraft_fach VALUES (22, 4);
INSERT INTO public.lehrkraft_fach VALUES (12, 1);
INSERT INTO public.lehrkraft_fach VALUES (12, 3);
INSERT INTO public.lehrkraft_fach VALUES (25, 10);
INSERT INTO public.lehrkraft_fach VALUES (25, 3);
INSERT INTO public.lehrkraft_fach VALUES (24, 10);
INSERT INTO public.lehrkraft_fach VALUES (24, 4);
INSERT INTO public.lehrkraft_fach VALUES (8, 4);
INSERT INTO public.lehrkraft_fach VALUES (8, 6);
INSERT INTO public.lehrkraft_fach VALUES (6, 1);
INSERT INTO public.lehrkraft_fach VALUES (6, 9);
INSERT INTO public.lehrkraft_fach VALUES (4, 5);
INSERT INTO public.lehrkraft_fach VALUES (4, 1);
INSERT INTO public.lehrkraft_fach VALUES (4, 2);
INSERT INTO public.lehrkraft_fach VALUES (11, 2);
INSERT INTO public.lehrkraft_fach VALUES (11, 8);
INSERT INTO public.lehrkraft_fach VALUES (11, 9);
INSERT INTO public.lehrkraft_fach VALUES (19, 11);
INSERT INTO public.lehrkraft_fach VALUES (19, 9);
INSERT INTO public.lehrkraft_fach VALUES (21, 10);
INSERT INTO public.lehrkraft_fach VALUES (21, 7);
INSERT INTO public.lehrkraft_fach VALUES (9, 3);
INSERT INTO public.lehrkraft_fach VALUES (9, 7);


--
-- TOC entry 5172 (class 0 OID 16704)
-- Dependencies: 226
-- Data for Name: raum; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.raum VALUES (5, 'R101', 30, 'STANDARD');
INSERT INTO public.raum VALUES (6, 'R102', 25, 'STANDARD');
INSERT INTO public.raum VALUES (7, 'R103', 20, 'STANDARD');
INSERT INTO public.raum VALUES (8, 'R104', 20, 'STANDARD');
INSERT INTO public.raum VALUES (9, 'R105', 30, 'STANDARD');
INSERT INTO public.raum VALUES (10, 'R106', 25, 'STANDARD');
INSERT INTO public.raum VALUES (14, 'R107', 25, 'STANDARD');
INSERT INTO public.raum VALUES (15, 'R108', 30, 'STANDARD');
INSERT INTO public.raum VALUES (16, 'R109', 35, 'STANDARD');
INSERT INTO public.raum VALUES (17, 'R110', 20, 'STANDARD');
INSERT INTO public.raum VALUES (11, 'Musik Raum 1', 33, 'MUSIK');
INSERT INTO public.raum VALUES (12, 'Sporthalle 1', 30, 'SPORTHALLE');
INSERT INTO public.raum VALUES (13, 'Bio-Raum 1', 25, 'STANDARD');
INSERT INTO public.raum VALUES (19, 'Sporthalle 2', 30, 'SPORTHALLE');
INSERT INTO public.raum VALUES (20, 'Bio-Raum 2', 30, 'STANDARD');
INSERT INTO public.raum VALUES (18, 'Chemie-Raum 1', 30, 'FACHRAUM_CHEMIE');
INSERT INTO public.raum VALUES (21, 'Chemie-Raum 2', 30, 'FACHRAUM_CHEMIE');
INSERT INTO public.raum VALUES (22, 'Musik Raum 2', 30, 'MUSIK');
INSERT INTO public.raum VALUES (23, 'R111', 30, 'STANDARD');
INSERT INTO public.raum VALUES (24, 'R112', 30, 'STANDARD');


--
-- TOC entry 5194 (class 0 OID 16975)
-- Dependencies: 248
-- Data for Name: schueler; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.schueler VALUES (3, '2012-05-14', 'Muster', 'AKTIV', 'Max', 1);
INSERT INTO public.schueler VALUES (4, '2011-12-02', 'Beispiel', 'AKTIV', 'Mario', 7);
INSERT INTO public.schueler VALUES (6, '2013-05-04', 'Neumann', 'AKTIV', 'Karl', 2);
INSERT INTO public.schueler VALUES (8, '2012-09-14', 'Lang', 'AKTIV', 'Kevin', 6);
INSERT INTO public.schueler VALUES (7, '2011-09-03', 'Neumann', 'AKTIV', 'Dmitri', 10);
INSERT INTO public.schueler VALUES (5, '2012-03-10', 'Roth', 'AKTIV', 'Daniel', 11);


--
-- TOC entry 5190 (class 0 OID 16870)
-- Dependencies: 244
-- Data for Name: schueler_benutzer; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.schueler_benutzer VALUES (54, 1);
INSERT INTO public.schueler_benutzer VALUES (55, 2);
INSERT INTO public.schueler_benutzer VALUES (56, 3);
INSERT INTO public.schueler_benutzer VALUES (57, 4);
INSERT INTO public.schueler_benutzer VALUES (58, 5);
INSERT INTO public.schueler_benutzer VALUES (59, 6);
INSERT INTO public.schueler_benutzer VALUES (60, 7);
INSERT INTO public.schueler_benutzer VALUES (61, 8);
INSERT INTO public.schueler_benutzer VALUES (62, 9);
INSERT INTO public.schueler_benutzer VALUES (63, 10);
INSERT INTO public.schueler_benutzer VALUES (64, 11);
INSERT INTO public.schueler_benutzer VALUES (65, 12);


--
-- TOC entry 5174 (class 0 OID 16716)
-- Dependencies: 228
-- Data for Name: schuljahr; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.schuljahr VALUES (1, '2025/2026', '2026-07-31', true, '2025-08-01');


--
-- TOC entry 5183 (class 0 OID 16794)
-- Dependencies: 237
-- Data for Name: sperrzeit; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 5176 (class 0 OID 16725)
-- Dependencies: 230
-- Data for Name: stunde; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.stunde VALUES (366, false, false, false, 1, 8, 6, 8, 2, 11);
INSERT INTO public.stunde VALUES (367, false, false, false, 7, 8, 23, 17, 2, 29);
INSERT INTO public.stunde VALUES (216, false, false, false, 3, 7, 9, 14, 2, 22);
INSERT INTO public.stunde VALUES (217, false, false, false, 1, 7, 6, 14, 2, 23);
INSERT INTO public.stunde VALUES (218, false, false, false, 1, 7, 6, 14, 2, 24);
INSERT INTO public.stunde VALUES (337, false, false, false, 10, 8, 24, 23, 2, 24);
INSERT INTO public.stunde VALUES (338, false, false, false, 1, 8, 4, 23, 2, 25);
INSERT INTO public.stunde VALUES (339, false, false, false, 1, 8, 4, 23, 2, 26);
INSERT INTO public.stunde VALUES (340, false, false, false, 1, 8, 4, 23, 2, 27);
INSERT INTO public.stunde VALUES (36, false, false, false, 1, 3, 4, 5, 2, 11);
INSERT INTO public.stunde VALUES (37, false, false, false, 1, 3, 4, 5, 2, 12);
INSERT INTO public.stunde VALUES (40, false, false, false, 6, 3, 5, 5, 2, 15);
INSERT INTO public.stunde VALUES (41, false, false, false, 6, 3, 5, 5, 2, 16);
INSERT INTO public.stunde VALUES (42, false, false, false, 7, 3, 9, 5, 2, 17);
INSERT INTO public.stunde VALUES (47, false, false, false, 10, 3, 25, 5, 2, 22);
INSERT INTO public.stunde VALUES (48, false, false, false, 9, 3, 6, 18, 2, 25);
INSERT INTO public.stunde VALUES (49, false, false, false, 9, 3, 6, 18, 2, 26);
INSERT INTO public.stunde VALUES (50, false, false, false, 4, 3, 17, 5, 2, 27);
INSERT INTO public.stunde VALUES (51, false, false, false, 4, 3, 17, 5, 2, 28);
INSERT INTO public.stunde VALUES (308, false, false, false, 6, 12, 8, 17, 2, 23);
INSERT INTO public.stunde VALUES (309, false, false, false, 6, 12, 8, 17, 2, 24);
INSERT INTO public.stunde VALUES (52, false, false, false, 11, 3, 19, 5, 2, 29);
INSERT INTO public.stunde VALUES (53, false, false, false, 11, 3, 19, 5, 2, 30);
INSERT INTO public.stunde VALUES (54, false, false, false, 2, 1, 11, 19, 2, 1);
INSERT INTO public.stunde VALUES (55, false, false, false, 2, 1, 11, 19, 2, 2);
INSERT INTO public.stunde VALUES (56, false, false, false, 3, 1, 5, 6, 2, 3);
INSERT INTO public.stunde VALUES (57, false, false, false, 3, 1, 5, 6, 2, 4);
INSERT INTO public.stunde VALUES (58, false, false, false, 1, 1, 7, 6, 2, 5);
INSERT INTO public.stunde VALUES (59, false, false, false, 1, 1, 7, 6, 2, 6);
INSERT INTO public.stunde VALUES (60, false, false, false, 6, 1, 8, 6, 2, 7);
INSERT INTO public.stunde VALUES (62, false, false, false, 8, 1, 11, 22, 2, 9);
INSERT INTO public.stunde VALUES (251, false, false, false, 1, 9, 7, 15, 2, 22);
INSERT INTO public.stunde VALUES (63, false, false, false, 8, 1, 11, 22, 2, 10);
INSERT INTO public.stunde VALUES (64, false, false, false, 4, 1, 8, 6, 2, 11);
INSERT INTO public.stunde VALUES (65, false, false, false, 4, 1, 8, 6, 2, 12);
INSERT INTO public.stunde VALUES (66, false, false, false, 5, 1, 4, 20, 2, 13);
INSERT INTO public.stunde VALUES (67, false, false, false, 5, 1, 4, 20, 2, 14);
INSERT INTO public.stunde VALUES (133, false, false, false, 4, 4, 20, 8, 2, 24);
INSERT INTO public.stunde VALUES (134, false, false, false, 9, 4, 6, 21, 2, 27);
INSERT INTO public.stunde VALUES (135, false, false, false, 9, 4, 6, 21, 2, 28);
INSERT INTO public.stunde VALUES (136, false, false, false, 7, 4, 9, 8, 2, 29);
INSERT INTO public.stunde VALUES (137, false, false, false, 7, 4, 9, 8, 2, 30);
INSERT INTO public.stunde VALUES (138, false, false, false, 4, 5, 24, 9, 2, 1);
INSERT INTO public.stunde VALUES (140, false, false, false, 4, 5, 24, 9, 2, 2);
INSERT INTO public.stunde VALUES (141, false, false, false, 9, 7, 19, 18, 2, 1);
INSERT INTO public.stunde VALUES (142, false, false, false, 9, 7, 19, 18, 2, 2);
INSERT INTO public.stunde VALUES (144, false, false, false, 3, 5, 12, 9, 2, 4);
INSERT INTO public.stunde VALUES (147, false, false, false, 8, 5, 11, 11, 2, 7);
INSERT INTO public.stunde VALUES (148, false, false, false, 1, 7, 6, 14, 2, 3);
INSERT INTO public.stunde VALUES (149, false, false, false, 8, 5, 11, 11, 2, 8);
INSERT INTO public.stunde VALUES (150, false, false, false, 1, 7, 6, 14, 2, 4);
INSERT INTO public.stunde VALUES (151, false, false, false, 1, 5, 4, 9, 2, 9);
INSERT INTO public.stunde VALUES (152, false, false, false, 1, 5, 4, 9, 2, 10);
INSERT INTO public.stunde VALUES (146, false, false, false, 2, 5, 16, 12, 2, 6);
INSERT INTO public.stunde VALUES (145, false, false, false, 2, 5, 16, 12, 2, 5);
INSERT INTO public.stunde VALUES (39, false, false, false, 5, 3, 16, 13, 2, 14);
INSERT INTO public.stunde VALUES (38, false, false, false, 5, 3, 16, 13, 2, 13);
INSERT INTO public.stunde VALUES (269, false, false, false, 2, 11, 16, 12, 2, 10);
INSERT INTO public.stunde VALUES (214, false, false, false, 5, 7, 16, 14, 2, 20);
INSERT INTO public.stunde VALUES (112, false, false, false, 2, 4, 16, 19, 2, 3);
INSERT INTO public.stunde VALUES (97, false, false, false, 5, 2, 16, 13, 2, 16);
INSERT INTO public.stunde VALUES (96, false, false, false, 5, 2, 16, 13, 2, 15);
INSERT INTO public.stunde VALUES (158, false, false, false, 5, 5, 10, 13, 2, 17);
INSERT INTO public.stunde VALUES (35, false, false, false, 8, 3, 10, 11, 2, 10);
INSERT INTO public.stunde VALUES (153, false, false, false, 6, 5, 5, 9, 2, 11);
INSERT INTO public.stunde VALUES (154, false, false, false, 6, 5, 5, 9, 2, 12);
INSERT INTO public.stunde VALUES (156, false, false, false, 3, 7, 9, 14, 2, 5);
INSERT INTO public.stunde VALUES (159, false, false, false, 3, 7, 9, 14, 2, 6);
INSERT INTO public.stunde VALUES (286, false, false, false, 10, 11, 24, 16, 2, 11);
INSERT INTO public.stunde VALUES (287, false, false, false, 10, 11, 24, 16, 2, 12);
INSERT INTO public.stunde VALUES (288, false, false, false, 4, 12, 8, 17, 2, 1);
INSERT INTO public.stunde VALUES (289, false, false, false, 4, 12, 8, 17, 2, 2);
INSERT INTO public.stunde VALUES (290, false, false, false, 9, 12, 19, 21, 2, 5);
INSERT INTO public.stunde VALUES (291, false, false, false, 9, 12, 19, 21, 2, 6);
INSERT INTO public.stunde VALUES (292, false, false, false, 1, 12, 12, 17, 2, 9);
INSERT INTO public.stunde VALUES (293, false, false, false, 1, 12, 12, 17, 2, 10);
INSERT INTO public.stunde VALUES (296, false, false, false, 3, 12, 25, 17, 2, 11);
INSERT INTO public.stunde VALUES (327, false, false, false, 3, 8, 9, 23, 2, 14);
INSERT INTO public.stunde VALUES (328, false, false, false, 11, 8, 17, 23, 2, 15);
INSERT INTO public.stunde VALUES (329, false, false, false, 11, 8, 17, 23, 2, 16);
INSERT INTO public.stunde VALUES (331, false, false, false, 4, 8, 17, 23, 2, 19);
INSERT INTO public.stunde VALUES (333, false, false, false, 4, 8, 17, 23, 2, 20);
INSERT INTO public.stunde VALUES (334, false, false, false, 9, 8, 6, 18, 2, 21);
INSERT INTO public.stunde VALUES (335, false, false, false, 9, 8, 6, 18, 2, 22);
INSERT INTO public.stunde VALUES (336, false, false, false, 10, 8, 24, 23, 2, 23);
INSERT INTO public.stunde VALUES (44, false, false, false, 3, 3, 12, 5, 2, 19);
INSERT INTO public.stunde VALUES (104, false, false, false, 11, 2, 20, 7, 2, 25);
INSERT INTO public.stunde VALUES (350, false, false, false, 1, 10, 7, 23, 2, 9);
INSERT INTO public.stunde VALUES (351, false, false, false, 1, 10, 7, 24, 2, 10);
INSERT INTO public.stunde VALUES (352, false, false, false, 2, 10, 18, 19, 2, 11);
INSERT INTO public.stunde VALUES (353, false, false, false, 2, 10, 18, 19, 2, 12);
INSERT INTO public.stunde VALUES (356, false, false, false, 6, 10, 8, 24, 2, 15);
INSERT INTO public.stunde VALUES (357, false, false, false, 6, 10, 8, 24, 2, 16);
INSERT INTO public.stunde VALUES (358, false, false, false, 11, 10, 20, 24, 2, 17);
INSERT INTO public.stunde VALUES (359, false, false, false, 11, 10, 20, 24, 2, 18);
INSERT INTO public.stunde VALUES (360, false, false, false, 4, 10, 22, 24, 2, 19);
INSERT INTO public.stunde VALUES (361, false, false, false, 4, 10, 22, 24, 2, 20);
INSERT INTO public.stunde VALUES (362, false, false, false, 3, 10, 23, 24, 2, 21);
INSERT INTO public.stunde VALUES (363, false, false, false, 3, 10, 23, 24, 2, 22);
INSERT INTO public.stunde VALUES (364, false, false, false, 1, 10, 7, 24, 2, 25);
INSERT INTO public.stunde VALUES (365, false, false, false, 1, 10, 7, 24, 2, 26);
INSERT INTO public.stunde VALUES (354, false, false, false, 8, 10, 10, 11, 2, 13);
INSERT INTO public.stunde VALUES (355, false, false, false, 8, 10, 10, 11, 2, 14);
INSERT INTO public.stunde VALUES (341, false, false, false, 7, 8, 9, 23, 2, 28);
INSERT INTO public.stunde VALUES (342, false, false, false, 7, 10, 23, 24, 2, 1);
INSERT INTO public.stunde VALUES (343, false, false, false, 7, 10, 23, 24, 2, 2);
INSERT INTO public.stunde VALUES (76, false, false, false, 11, 1, 17, 6, 2, 23);
INSERT INTO public.stunde VALUES (77, false, false, false, 11, 1, 17, 6, 2, 24);
INSERT INTO public.stunde VALUES (78, false, false, false, 9, 1, 19, 21, 2, 25);
INSERT INTO public.stunde VALUES (29, false, false, false, 1, 3, 4, 5, 2, 3);
INSERT INTO public.stunde VALUES (30, false, false, false, 1, 3, 4, 5, 2, 4);
INSERT INTO public.stunde VALUES (32, false, false, false, 3, 3, 12, 5, 2, 6);
INSERT INTO public.stunde VALUES (344, false, false, false, 4, 10, 22, 24, 2, 3);
INSERT INTO public.stunde VALUES (345, false, false, false, 4, 10, 22, 24, 2, 4);
INSERT INTO public.stunde VALUES (346, false, false, false, 3, 10, 23, 24, 2, 5);
INSERT INTO public.stunde VALUES (347, false, false, false, 3, 10, 23, 24, 2, 6);
INSERT INTO public.stunde VALUES (348, false, false, false, 9, 10, 19, 18, 2, 7);
INSERT INTO public.stunde VALUES (349, false, false, false, 9, 10, 19, 18, 2, 8);
INSERT INTO public.stunde VALUES (125, false, false, false, 5, 4, 10, 20, 2, 16);
INSERT INTO public.stunde VALUES (185, false, false, false, 8, 6, 10, 22, 2, 8);
INSERT INTO public.stunde VALUES (184, false, false, false, 8, 6, 10, 22, 2, 7);
INSERT INTO public.stunde VALUES (248, false, false, false, 5, 9, 10, 20, 2, 20);
INSERT INTO public.stunde VALUES (249, false, false, false, 5, 9, 10, 20, 2, 19);
INSERT INTO public.stunde VALUES (253, false, false, false, 1, 9, 7, 15, 2, 16);
INSERT INTO public.stunde VALUES (254, false, false, false, 3, 9, 23, 15, 2, 23);
INSERT INTO public.stunde VALUES (255, false, false, false, 3, 9, 23, 15, 2, 24);
INSERT INTO public.stunde VALUES (93, false, false, false, 8, 2, 10, 11, 2, 12);
INSERT INTO public.stunde VALUES (202, false, false, false, 3, 6, 5, 10, 2, 22);
INSERT INTO public.stunde VALUES (203, false, false, false, 4, 6, 8, 10, 2, 25);
INSERT INTO public.stunde VALUES (204, false, false, false, 4, 6, 8, 10, 2, 26);
INSERT INTO public.stunde VALUES (69, false, false, false, 7, 1, 21, 6, 2, 16);
INSERT INTO public.stunde VALUES (283, false, false, false, 3, 11, 12, 16, 2, 24);
INSERT INTO public.stunde VALUES (282, false, false, false, 3, 11, 12, 16, 2, 23);
INSERT INTO public.stunde VALUES (70, false, false, false, 3, 1, 5, 6, 2, 17);
INSERT INTO public.stunde VALUES (71, false, false, false, 3, 1, 5, 6, 2, 18);
INSERT INTO public.stunde VALUES (79, false, false, false, 9, 1, 19, 21, 2, 26);
INSERT INTO public.stunde VALUES (80, false, false, false, 10, 1, 24, 6, 2, 29);
INSERT INTO public.stunde VALUES (81, false, false, false, 10, 1, 24, 6, 2, 30);
INSERT INTO public.stunde VALUES (275, false, false, false, 8, 11, 11, 11, 2, 20);
INSERT INTO public.stunde VALUES (276, false, false, false, 4, 11, 24, 16, 2, 17);
INSERT INTO public.stunde VALUES (277, false, false, false, 4, 11, 24, 16, 2, 18);
INSERT INTO public.stunde VALUES (278, false, false, false, 6, 11, 5, 16, 2, 25);
INSERT INTO public.stunde VALUES (279, false, false, false, 6, 11, 5, 16, 2, 26);
INSERT INTO public.stunde VALUES (280, false, false, false, 5, 11, 4, 13, 2, 21);
INSERT INTO public.stunde VALUES (281, false, false, false, 5, 11, 4, 13, 2, 22);
INSERT INTO public.stunde VALUES (284, false, false, false, 11, 11, 20, 16, 2, 27);
INSERT INTO public.stunde VALUES (271, false, false, false, 7, 11, 21, 16, 2, 14);
INSERT INTO public.stunde VALUES (272, false, false, false, 1, 11, 18, 16, 2, 15);
INSERT INTO public.stunde VALUES (273, false, false, false, 1, 11, 18, 16, 2, 16);
INSERT INTO public.stunde VALUES (274, false, false, false, 8, 11, 11, 11, 2, 19);
INSERT INTO public.stunde VALUES (124, false, false, false, 5, 4, 10, 20, 2, 15);
INSERT INTO public.stunde VALUES (160, false, false, false, 5, 5, 10, 13, 2, 18);
INSERT INTO public.stunde VALUES (89, false, false, false, 3, 2, 25, 7, 2, 8);
INSERT INTO public.stunde VALUES (90, false, false, false, 1, 2, 6, 7, 2, 9);
INSERT INTO public.stunde VALUES (91, false, false, false, 1, 2, 6, 7, 2, 10);
INSERT INTO public.stunde VALUES (94, false, false, false, 6, 2, 5, 7, 2, 13);
INSERT INTO public.stunde VALUES (95, false, false, false, 6, 2, 5, 7, 2, 14);
INSERT INTO public.stunde VALUES (98, false, false, false, 4, 2, 22, 7, 2, 17);
INSERT INTO public.stunde VALUES (99, false, false, false, 4, 2, 22, 7, 2, 18);
INSERT INTO public.stunde VALUES (100, false, false, false, 7, 2, 23, 7, 2, 19);
INSERT INTO public.stunde VALUES (219, false, false, false, 11, 7, 17, 14, 2, 25);
INSERT INTO public.stunde VALUES (28, false, false, false, 4, 3, 17, 5, 2, 7);
INSERT INTO public.stunde VALUES (33, false, false, false, 4, 3, 17, 5, 2, 8);
INSERT INTO public.stunde VALUES (43, false, false, false, 7, 3, 9, 5, 2, 18);
INSERT INTO public.stunde VALUES (46, false, false, false, 10, 3, 25, 5, 2, 21);
INSERT INTO public.stunde VALUES (19, false, false, false, 2, 3, 16, 12, 2, 2);
INSERT INTO public.stunde VALUES (113, false, false, false, 2, 4, 16, 19, 2, 4);
INSERT INTO public.stunde VALUES (268, false, false, false, 2, 11, 16, 12, 2, 9);
INSERT INTO public.stunde VALUES (306, false, false, false, 5, 12, 10, 20, 2, 21);
INSERT INTO public.stunde VALUES (307, false, false, false, 5, 12, 10, 20, 2, 22);
INSERT INTO public.stunde VALUES (92, false, false, false, 8, 2, 10, 11, 2, 11);
INSERT INTO public.stunde VALUES (34, false, false, false, 8, 3, 10, 11, 2, 9);
INSERT INTO public.stunde VALUES (82, false, false, false, 1, 2, 6, 7, 2, 1);
INSERT INTO public.stunde VALUES (83, false, false, false, 1, 2, 6, 7, 2, 2);
INSERT INTO public.stunde VALUES (84, false, false, false, 2, 2, 18, 12, 2, 3);
INSERT INTO public.stunde VALUES (261, false, false, false, 1, 11, 18, 16, 2, 2);
INSERT INTO public.stunde VALUES (262, false, false, false, 4, 11, 24, 16, 2, 3);
INSERT INTO public.stunde VALUES (263, false, false, false, 4, 11, 24, 16, 2, 4);
INSERT INTO public.stunde VALUES (264, false, false, false, 9, 11, 6, 18, 2, 5);
INSERT INTO public.stunde VALUES (265, false, false, false, 9, 11, 6, 18, 2, 6);
INSERT INTO public.stunde VALUES (270, false, false, false, 7, 11, 21, 16, 2, 13);
INSERT INTO public.stunde VALUES (101, false, false, false, 7, 2, 23, 7, 2, 20);
INSERT INTO public.stunde VALUES (102, false, false, false, 3, 2, 25, 7, 2, 23);
INSERT INTO public.stunde VALUES (73, false, false, false, 1, 1, 7, 6, 2, 20);
INSERT INTO public.stunde VALUES (74, false, false, false, 4, 1, 8, 6, 2, 21);
INSERT INTO public.stunde VALUES (75, false, false, false, 4, 1, 8, 6, 2, 22);
INSERT INTO public.stunde VALUES (45, false, false, false, 3, 3, 12, 5, 2, 20);
INSERT INTO public.stunde VALUES (72, false, false, false, 1, 1, 7, 6, 2, 19);
INSERT INTO public.stunde VALUES (103, false, false, false, 3, 2, 25, 7, 2, 24);
INSERT INTO public.stunde VALUES (213, false, false, false, 5, 7, 16, 13, 2, 19);
INSERT INTO public.stunde VALUES (105, false, false, false, 11, 2, 20, 7, 2, 26);
INSERT INTO public.stunde VALUES (106, false, false, false, 9, 2, 19, 18, 2, 27);
INSERT INTO public.stunde VALUES (107, false, false, false, 9, 2, 19, 18, 2, 28);
INSERT INTO public.stunde VALUES (108, false, false, false, 10, 2, 21, 7, 2, 29);
INSERT INTO public.stunde VALUES (109, false, false, false, 10, 2, 21, 7, 2, 30);
INSERT INTO public.stunde VALUES (110, false, false, false, 3, 4, 9, 8, 2, 1);
INSERT INTO public.stunde VALUES (111, false, false, false, 3, 4, 9, 8, 2, 2);
INSERT INTO public.stunde VALUES (114, false, false, false, 1, 4, 18, 8, 2, 5);
INSERT INTO public.stunde VALUES (115, false, false, false, 1, 4, 18, 8, 2, 6);
INSERT INTO public.stunde VALUES (116, false, false, false, 4, 4, 20, 8, 2, 7);
INSERT INTO public.stunde VALUES (117, false, false, false, 4, 4, 20, 8, 2, 8);
INSERT INTO public.stunde VALUES (118, false, false, false, 3, 4, 9, 8, 2, 9);
INSERT INTO public.stunde VALUES (119, false, false, false, 3, 4, 9, 8, 2, 10);
INSERT INTO public.stunde VALUES (120, false, false, false, 8, 4, 11, 22, 2, 11);
INSERT INTO public.stunde VALUES (121, false, false, false, 8, 4, 11, 22, 2, 12);
INSERT INTO public.stunde VALUES (122, false, false, false, 10, 4, 25, 8, 2, 13);
INSERT INTO public.stunde VALUES (123, false, false, false, 10, 4, 25, 8, 2, 14);
INSERT INTO public.stunde VALUES (126, false, false, false, 1, 4, 7, 8, 2, 17);
INSERT INTO public.stunde VALUES (127, false, false, false, 1, 4, 7, 8, 2, 18);
INSERT INTO public.stunde VALUES (128, false, false, false, 6, 4, 8, 8, 2, 19);
INSERT INTO public.stunde VALUES (129, false, false, false, 6, 4, 8, 8, 2, 20);
INSERT INTO public.stunde VALUES (130, false, false, false, 11, 4, 19, 8, 2, 21);
INSERT INTO public.stunde VALUES (131, false, false, false, 11, 4, 19, 8, 2, 22);
INSERT INTO public.stunde VALUES (161, false, false, false, 1, 5, 4, 9, 2, 19);
INSERT INTO public.stunde VALUES (162, false, false, false, 1, 5, 4, 9, 2, 20);
INSERT INTO public.stunde VALUES (163, false, false, false, 7, 5, 21, 9, 2, 21);
INSERT INTO public.stunde VALUES (164, false, false, false, 7, 5, 21, 9, 2, 22);
INSERT INTO public.stunde VALUES (31, false, false, false, 3, 3, 12, 5, 2, 5);
INSERT INTO public.stunde VALUES (165, false, false, false, 11, 5, 7, 9, 2, 23);
INSERT INTO public.stunde VALUES (166, false, false, false, 11, 5, 7, 9, 2, 24);
INSERT INTO public.stunde VALUES (167, false, false, false, 2, 7, 4, 12, 2, 7);
INSERT INTO public.stunde VALUES (168, false, false, false, 10, 5, 25, 9, 2, 25);
INSERT INTO public.stunde VALUES (169, false, false, false, 10, 5, 25, 9, 2, 26);
INSERT INTO public.stunde VALUES (170, false, false, false, 2, 7, 4, 12, 2, 8);
INSERT INTO public.stunde VALUES (132, false, false, false, 4, 4, 20, 8, 2, 23);
INSERT INTO public.stunde VALUES (187, false, false, false, 3, 6, 5, 10, 2, 10);
INSERT INTO public.stunde VALUES (171, false, false, false, 4, 5, 24, 9, 2, 27);
INSERT INTO public.stunde VALUES (172, false, false, false, 4, 5, 24, 9, 2, 28);
INSERT INTO public.stunde VALUES (173, false, false, false, 9, 5, 6, 18, 2, 29);
INSERT INTO public.stunde VALUES (174, false, false, false, 9, 5, 6, 18, 2, 30);
INSERT INTO public.stunde VALUES (175, false, false, false, 4, 7, 17, 14, 2, 9);
INSERT INTO public.stunde VALUES (176, false, false, false, 1, 6, 12, 10, 2, 1);
INSERT INTO public.stunde VALUES (177, false, false, false, 1, 6, 12, 10, 2, 2);
INSERT INTO public.stunde VALUES (178, false, false, false, 4, 6, 8, 10, 2, 3);
INSERT INTO public.stunde VALUES (179, false, false, false, 4, 7, 17, 14, 2, 10);
INSERT INTO public.stunde VALUES (180, false, false, false, 4, 6, 8, 10, 2, 4);
INSERT INTO public.stunde VALUES (181, false, false, false, 2, 6, 11, 10, 2, 5);
INSERT INTO public.stunde VALUES (182, false, false, false, 2, 6, 11, 10, 2, 6);
INSERT INTO public.stunde VALUES (183, false, false, false, 7, 7, 9, 14, 2, 11);
INSERT INTO public.stunde VALUES (186, false, false, false, 3, 6, 5, 10, 2, 9);
INSERT INTO public.stunde VALUES (220, false, false, false, 11, 7, 17, 14, 2, 26);
INSERT INTO public.stunde VALUES (221, false, false, false, 10, 7, 25, 14, 2, 27);
INSERT INTO public.stunde VALUES (222, false, false, false, 10, 7, 25, 14, 2, 28);
INSERT INTO public.stunde VALUES (229, false, false, false, 2, 9, 18, 19, 2, 7);
INSERT INTO public.stunde VALUES (230, false, false, false, 2, 9, 18, 19, 2, 8);
INSERT INTO public.stunde VALUES (231, false, false, false, 4, 9, 20, 15, 2, 9);
INSERT INTO public.stunde VALUES (232, false, false, false, 4, 9, 20, 15, 2, 10);
INSERT INTO public.stunde VALUES (233, false, false, false, 7, 9, 21, 15, 2, 11);
INSERT INTO public.stunde VALUES (234, false, false, false, 7, 9, 21, 15, 2, 12);
INSERT INTO public.stunde VALUES (236, false, false, false, 4, 9, 20, 15, 2, 1);
INSERT INTO public.stunde VALUES (237, false, false, false, 4, 9, 20, 15, 2, 2);
INSERT INTO public.stunde VALUES (238, false, false, false, 9, 9, 19, 21, 2, 3);
INSERT INTO public.stunde VALUES (239, false, false, false, 9, 9, 19, 21, 2, 4);
INSERT INTO public.stunde VALUES (240, false, false, false, 6, 9, 8, 15, 2, 5);
INSERT INTO public.stunde VALUES (241, false, false, false, 6, 9, 8, 15, 2, 6);
INSERT INTO public.stunde VALUES (242, false, false, false, 11, 9, 17, 15, 2, 13);
INSERT INTO public.stunde VALUES (243, false, false, false, 11, 9, 17, 15, 2, 14);
INSERT INTO public.stunde VALUES (244, false, false, false, 8, 9, 11, 22, 2, 17);
INSERT INTO public.stunde VALUES (205, false, false, false, 10, 6, 21, 10, 2, 27);
INSERT INTO public.stunde VALUES (206, false, false, false, 10, 6, 21, 10, 2, 28);
INSERT INTO public.stunde VALUES (207, false, false, false, 9, 6, 11, 21, 2, 29);
INSERT INTO public.stunde VALUES (208, false, false, false, 9, 6, 11, 10, 2, 30);
INSERT INTO public.stunde VALUES (245, false, false, false, 8, 9, 11, 22, 2, 18);
INSERT INTO public.stunde VALUES (250, false, false, false, 1, 9, 7, 16, 2, 21);
INSERT INTO public.stunde VALUES (252, false, false, false, 1, 9, 7, 15, 2, 15);
INSERT INTO public.stunde VALUES (68, false, false, false, 7, 1, 21, 6, 2, 15);
INSERT INTO public.stunde VALUES (311, false, false, false, 1, 12, 12, 17, 2, 26);
INSERT INTO public.stunde VALUES (285, false, false, false, 11, 11, 20, 16, 2, 28);
INSERT INTO public.stunde VALUES (297, false, false, false, 3, 12, 25, 17, 2, 12);
INSERT INTO public.stunde VALUES (298, false, false, false, 3, 12, 25, 17, 2, 15);
INSERT INTO public.stunde VALUES (299, false, false, false, 3, 12, 25, 17, 2, 16);
INSERT INTO public.stunde VALUES (302, false, false, false, 2, 12, 11, 19, 2, 13);
INSERT INTO public.stunde VALUES (303, false, false, false, 2, 12, 11, 19, 2, 14);
INSERT INTO public.stunde VALUES (300, false, false, false, 7, 12, 23, 17, 2, 17);
INSERT INTO public.stunde VALUES (301, false, false, false, 7, 12, 23, 17, 2, 18);
INSERT INTO public.stunde VALUES (304, false, false, false, 10, 12, 25, 17, 2, 19);
INSERT INTO public.stunde VALUES (305, false, false, false, 10, 12, 25, 17, 2, 20);
INSERT INTO public.stunde VALUES (312, false, false, false, 4, 12, 8, 17, 2, 27);
INSERT INTO public.stunde VALUES (313, false, false, false, 4, 12, 8, 17, 2, 28);
INSERT INTO public.stunde VALUES (314, false, false, false, 11, 12, 7, 17, 2, 3);
INSERT INTO public.stunde VALUES (315, false, false, false, 11, 12, 7, 17, 2, 4);
INSERT INTO public.stunde VALUES (316, false, false, false, 4, 8, 17, 23, 2, 1);
INSERT INTO public.stunde VALUES (317, false, false, false, 4, 8, 17, 23, 2, 2);
INSERT INTO public.stunde VALUES (318, false, false, false, 8, 8, 11, 11, 2, 3);
INSERT INTO public.stunde VALUES (319, false, false, false, 8, 8, 11, 11, 2, 4);
INSERT INTO public.stunde VALUES (320, false, false, false, 6, 8, 5, 23, 2, 5);
INSERT INTO public.stunde VALUES (321, false, false, false, 6, 8, 5, 23, 2, 6);
INSERT INTO public.stunde VALUES (322, false, false, false, 3, 8, 9, 23, 2, 7);
INSERT INTO public.stunde VALUES (323, false, false, false, 3, 8, 9, 23, 2, 8);
INSERT INTO public.stunde VALUES (324, false, false, false, 2, 8, 18, 19, 2, 9);
INSERT INTO public.stunde VALUES (325, false, false, false, 2, 8, 18, 19, 2, 10);
INSERT INTO public.stunde VALUES (326, false, false, false, 3, 8, 9, 23, 2, 13);
INSERT INTO public.stunde VALUES (85, false, false, false, 2, 2, 18, 12, 2, 4);
INSERT INTO public.stunde VALUES (86, false, false, false, 4, 2, 22, 7, 2, 5);
INSERT INTO public.stunde VALUES (87, false, false, false, 4, 2, 22, 7, 2, 6);
INSERT INTO public.stunde VALUES (88, false, false, false, 3, 2, 25, 7, 2, 7);
INSERT INTO public.stunde VALUES (310, false, false, false, 1, 12, 12, 17, 2, 25);
INSERT INTO public.stunde VALUES (267, false, false, false, 3, 11, 12, 16, 2, 8);
INSERT INTO public.stunde VALUES (61, true, false, false, 6, 1, 8, 6, 2, 8);
INSERT INTO public.stunde VALUES (266, false, false, false, 3, 11, 12, 16, 2, 7);
INSERT INTO public.stunde VALUES (256, false, false, false, 3, 9, 23, 15, 2, 25);
INSERT INTO public.stunde VALUES (257, false, false, false, 3, 9, 23, 15, 2, 26);
INSERT INTO public.stunde VALUES (258, false, false, false, 10, 9, 25, 15, 2, 29);
INSERT INTO public.stunde VALUES (259, false, false, false, 10, 9, 25, 15, 2, 30);
INSERT INTO public.stunde VALUES (260, false, false, false, 1, 11, 18, 16, 2, 1);
INSERT INTO public.stunde VALUES (155, false, false, false, 3, 5, 12, 9, 2, 13);
INSERT INTO public.stunde VALUES (157, false, false, false, 3, 5, 12, 9, 2, 14);
INSERT INTO public.stunde VALUES (188, false, false, false, 11, 6, 20, 10, 2, 11);
INSERT INTO public.stunde VALUES (189, false, false, false, 7, 7, 9, 14, 2, 12);
INSERT INTO public.stunde VALUES (143, false, false, false, 3, 5, 12, 9, 2, 3);
INSERT INTO public.stunde VALUES (190, false, false, false, 11, 6, 20, 10, 2, 12);
INSERT INTO public.stunde VALUES (191, false, false, false, 1, 6, 22, 10, 2, 13);
INSERT INTO public.stunde VALUES (192, false, false, false, 1, 6, 22, 10, 2, 14);
INSERT INTO public.stunde VALUES (18, false, false, false, 2, 3, 16, 12, 2, 1);
INSERT INTO public.stunde VALUES (193, false, false, false, 6, 7, 8, 14, 2, 13);
INSERT INTO public.stunde VALUES (194, false, false, false, 7, 6, 23, 10, 2, 15);
INSERT INTO public.stunde VALUES (195, false, false, false, 7, 6, 23, 10, 2, 16);
INSERT INTO public.stunde VALUES (196, false, false, false, 5, 6, 4, 20, 2, 17);
INSERT INTO public.stunde VALUES (197, false, false, false, 5, 6, 4, 20, 2, 18);
INSERT INTO public.stunde VALUES (198, false, false, false, 6, 6, 5, 10, 2, 19);
INSERT INTO public.stunde VALUES (199, false, false, false, 6, 6, 5, 10, 2, 20);
INSERT INTO public.stunde VALUES (200, false, false, false, 6, 7, 8, 14, 2, 14);
INSERT INTO public.stunde VALUES (201, false, false, false, 3, 6, 5, 10, 2, 21);
INSERT INTO public.stunde VALUES (209, false, false, false, 8, 7, 11, 11, 2, 15);
INSERT INTO public.stunde VALUES (210, false, false, false, 8, 7, 11, 11, 2, 16);
INSERT INTO public.stunde VALUES (211, false, false, false, 4, 7, 17, 14, 2, 17);
INSERT INTO public.stunde VALUES (212, false, false, false, 4, 7, 17, 14, 2, 18);
INSERT INTO public.stunde VALUES (215, false, false, false, 3, 7, 9, 14, 2, 21);


--
-- TOC entry 5178 (class 0 OID 16736)
-- Dependencies: 232
-- Data for Name: stundenplan; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.stundenplan VALUES (2, '2025-08-01', 1);


--
-- TOC entry 5192 (class 0 OID 16879)
-- Dependencies: 246
-- Data for Name: vertretung; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 5180 (class 0 OID 16744)
-- Dependencies: 234
-- Data for Name: zeitslot; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.zeitslot VALUES (1, '08:45:00', '08:00:00', 1, 'MONTAG');
INSERT INTO public.zeitslot VALUES (2, '09:30:00', '08:45:00', 2, 'MONTAG');
INSERT INTO public.zeitslot VALUES (3, '10:30:00', '09:45:00', 3, 'MONTAG');
INSERT INTO public.zeitslot VALUES (4, '11:15:00', '10:30:00', 4, 'MONTAG');
INSERT INTO public.zeitslot VALUES (5, '12:15:00', '11:30:00', 5, 'MONTAG');
INSERT INTO public.zeitslot VALUES (6, '13:00:00', '12:15:00', 6, 'MONTAG');
INSERT INTO public.zeitslot VALUES (7, '08:45:00', '08:00:00', 1, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (8, '09:30:00', '08:45:00', 2, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (9, '10:30:00', '09:45:00', 3, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (10, '11:15:00', '10:30:00', 4, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (11, '12:15:00', '11:30:00', 5, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (12, '13:00:00', '12:15:00', 6, 'DIENSTAG');
INSERT INTO public.zeitslot VALUES (13, '08:45:00', '08:00:00', 1, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (14, '09:30:00', '08:45:00', 2, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (15, '10:30:00', '09:45:00', 3, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (16, '11:15:00', '10:30:00', 4, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (17, '12:15:00', '11:30:00', 5, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (18, '13:00:00', '12:15:00', 6, 'MITTWOCH');
INSERT INTO public.zeitslot VALUES (19, '08:45:00', '08:00:00', 1, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (20, '09:30:00', '08:45:00', 2, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (21, '10:30:00', '09:45:00', 3, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (22, '11:15:00', '10:30:00', 4, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (23, '12:15:00', '11:30:00', 5, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (24, '13:00:00', '12:15:00', 6, 'DONNERSTAG');
INSERT INTO public.zeitslot VALUES (25, '08:45:00', '08:00:00', 1, 'FREITAG');
INSERT INTO public.zeitslot VALUES (26, '09:30:00', '08:45:00', 2, 'FREITAG');
INSERT INTO public.zeitslot VALUES (27, '10:30:00', '09:45:00', 3, 'FREITAG');
INSERT INTO public.zeitslot VALUES (28, '11:15:00', '10:30:00', 4, 'FREITAG');
INSERT INTO public.zeitslot VALUES (29, '12:15:00', '11:30:00', 5, 'FREITAG');
INSERT INTO public.zeitslot VALUES (30, '13:00:00', '12:15:00', 6, 'FREITAG');


--
-- TOC entry 5217 (class 0 OID 0)
-- Dependencies: 238
-- Name: abwesenheit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.abwesenheit_id_seq', 32, true);


--
-- TOC entry 5218 (class 0 OID 0)
-- Dependencies: 241
-- Name: benutzer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.benutzer_id_seq', 73, true);


--
-- TOC entry 5219 (class 0 OID 0)
-- Dependencies: 249
-- Name: erziehungsberechtigter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.erziehungsberechtigter_id_seq', 7, true);


--
-- TOC entry 5220 (class 0 OID 0)
-- Dependencies: 219
-- Name: fach_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.fach_id_seq', 26, true);


--
-- TOC entry 5221 (class 0 OID 0)
-- Dependencies: 221
-- Name: klasse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.klasse_id_seq', 17, true);


--
-- TOC entry 5222 (class 0 OID 0)
-- Dependencies: 223
-- Name: lehrkraft_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.lehrkraft_id_seq', 29, true);


--
-- TOC entry 5223 (class 0 OID 0)
-- Dependencies: 225
-- Name: raum_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.raum_id_seq', 28, true);


--
-- TOC entry 5224 (class 0 OID 0)
-- Dependencies: 247
-- Name: schueler_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.schueler_id_seq', 8, true);


--
-- TOC entry 5225 (class 0 OID 0)
-- Dependencies: 227
-- Name: schuljahr_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.schuljahr_id_seq', 1, true);


--
-- TOC entry 5226 (class 0 OID 0)
-- Dependencies: 236
-- Name: sperrzeit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.sperrzeit_id_seq', 13, true);


--
-- TOC entry 5227 (class 0 OID 0)
-- Dependencies: 229
-- Name: stunde_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.stunde_id_seq', 382, true);


--
-- TOC entry 5228 (class 0 OID 0)
-- Dependencies: 231
-- Name: stundenplan_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.stundenplan_id_seq', 2, true);


--
-- TOC entry 5229 (class 0 OID 0)
-- Dependencies: 245
-- Name: vertretung_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.vertretung_id_seq', 155, true);


--
-- TOC entry 5230 (class 0 OID 0)
-- Dependencies: 233
-- Name: zeitslot_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.zeitslot_id_seq', 30, true);


--
-- TOC entry 4977 (class 2606 OID 16841)
-- Name: abwesenheit abwesenheit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.abwesenheit
    ADD CONSTRAINT abwesenheit_pkey PRIMARY KEY (id);


--
-- TOC entry 4979 (class 2606 OID 16847)
-- Name: admin_benutzer admin_benutzer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_benutzer
    ADD CONSTRAINT admin_benutzer_pkey PRIMARY KEY (id);


--
-- TOC entry 4981 (class 2606 OID 16862)
-- Name: benutzer benutzer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.benutzer
    ADD CONSTRAINT benutzer_pkey PRIMARY KEY (id);


--
-- TOC entry 4995 (class 2606 OID 17007)
-- Name: erziehungsberechtigter erziehungsberechtigter_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.erziehungsberechtigter
    ADD CONSTRAINT erziehungsberechtigter_pkey PRIMARY KEY (id);


--
-- TOC entry 4959 (class 2606 OID 16681)
-- Name: fach fach_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fach
    ADD CONSTRAINT fach_pkey PRIMARY KEY (id);


--
-- TOC entry 4961 (class 2606 OID 16691)
-- Name: klasse klasse_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.klasse
    ADD CONSTRAINT klasse_pkey PRIMARY KEY (id);


--
-- TOC entry 4985 (class 2606 OID 16869)
-- Name: lehrer_benutzer lehrer_benutzer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrer_benutzer
    ADD CONSTRAINT lehrer_benutzer_pkey PRIMARY KEY (id);


--
-- TOC entry 4963 (class 2606 OID 16702)
-- Name: lehrkraft lehrkraft_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrkraft
    ADD CONSTRAINT lehrkraft_pkey PRIMARY KEY (id);


--
-- TOC entry 4965 (class 2606 OID 16714)
-- Name: raum raum_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.raum
    ADD CONSTRAINT raum_pkey PRIMARY KEY (id);


--
-- TOC entry 4989 (class 2606 OID 16876)
-- Name: schueler_benutzer schueler_benutzer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler_benutzer
    ADD CONSTRAINT schueler_benutzer_pkey PRIMARY KEY (id);


--
-- TOC entry 4993 (class 2606 OID 16987)
-- Name: schueler schueler_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler
    ADD CONSTRAINT schueler_pkey PRIMARY KEY (id);


--
-- TOC entry 4967 (class 2606 OID 16723)
-- Name: schuljahr schuljahr_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schuljahr
    ADD CONSTRAINT schuljahr_pkey PRIMARY KEY (id);


--
-- TOC entry 4975 (class 2606 OID 16802)
-- Name: sperrzeit sperrzeit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sperrzeit
    ADD CONSTRAINT sperrzeit_pkey PRIMARY KEY (id);


--
-- TOC entry 4969 (class 2606 OID 16734)
-- Name: stunde stunde_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT stunde_pkey PRIMARY KEY (id);


--
-- TOC entry 4971 (class 2606 OID 16742)
-- Name: stundenplan stundenplan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stundenplan
    ADD CONSTRAINT stundenplan_pkey PRIMARY KEY (id);


--
-- TOC entry 4983 (class 2606 OID 16893)
-- Name: benutzer uk_a9pmer7fnastb2t6o0ag5gbpj; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.benutzer
    ADD CONSTRAINT uk_a9pmer7fnastb2t6o0ag5gbpj UNIQUE (benutzername);


--
-- TOC entry 4987 (class 2606 OID 16895)
-- Name: lehrer_benutzer uk_haeu77s8ywdv7lvgl7wqplwqr; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrer_benutzer
    ADD CONSTRAINT uk_haeu77s8ywdv7lvgl7wqplwqr UNIQUE (lehrkraft_id);


--
-- TOC entry 4991 (class 2606 OID 16891)
-- Name: vertretung vertretung_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vertretung
    ADD CONSTRAINT vertretung_pkey PRIMARY KEY (id);


--
-- TOC entry 4973 (class 2606 OID 16752)
-- Name: zeitslot zeitslot_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.zeitslot
    ADD CONSTRAINT zeitslot_pkey PRIMARY KEY (id);


--
-- TOC entry 5004 (class 2606 OID 16809)
-- Name: lehrkraft_fach fk1918du8o22m8brr2dl4b2j3wi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrkraft_fach
    ADD CONSTRAINT fk1918du8o22m8brr2dl4b2j3wi FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 5012 (class 2606 OID 16921)
-- Name: schueler_benutzer fk3rg5o0kx1gcull5pcjsxbwb2g; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler_benutzer
    ADD CONSTRAINT fk3rg5o0kx1gcull5pcjsxbwb2g FOREIGN KEY (id) REFERENCES public.benutzer(id);


--
-- TOC entry 5014 (class 2606 OID 16926)
-- Name: vertretung fk73tv8vj04oq4rimaa0mpw6qao; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vertretung
    ADD CONSTRAINT fk73tv8vj04oq4rimaa0mpw6qao FOREIGN KEY (stunde_id) REFERENCES public.stunde(id);


--
-- TOC entry 5010 (class 2606 OID 16906)
-- Name: lehrer_benutzer fk792u1asaiguijynnvhadyxk1m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrer_benutzer
    ADD CONSTRAINT fk792u1asaiguijynnvhadyxk1m FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 5009 (class 2606 OID 16901)
-- Name: admin_benutzer fk7jmg1p5ootf4y69dmc21a662r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_benutzer
    ADD CONSTRAINT fk7jmg1p5ootf4y69dmc21a662r FOREIGN KEY (id) REFERENCES public.benutzer(id);


--
-- TOC entry 5003 (class 2606 OID 16965)
-- Name: stundenplan fka8l71bc454w9cjhyykd0e2b6j; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stundenplan
    ADD CONSTRAINT fka8l71bc454w9cjhyykd0e2b6j FOREIGN KEY (schuljahr_id) REFERENCES public.schuljahr(id);


--
-- TOC entry 5013 (class 2606 OID 16916)
-- Name: schueler_benutzer fkbcexdy46dtxttfnug0bhn5l1v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler_benutzer
    ADD CONSTRAINT fkbcexdy46dtxttfnug0bhn5l1v FOREIGN KEY (klasse_id) REFERENCES public.klasse(id);


--
-- TOC entry 5008 (class 2606 OID 16896)
-- Name: abwesenheit fkdj2wfx695gsd9jmyoi9kei153; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.abwesenheit
    ADD CONSTRAINT fkdj2wfx695gsd9jmyoi9kei153 FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 5006 (class 2606 OID 16814)
-- Name: sperrzeit fkik2u48yxsfu6wso8g22kwrr80; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sperrzeit
    ADD CONSTRAINT fkik2u48yxsfu6wso8g22kwrr80 FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 5011 (class 2606 OID 16911)
-- Name: lehrer_benutzer fkjlxql25f985gcfh9h5lptog4w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrer_benutzer
    ADD CONSTRAINT fkjlxql25f985gcfh9h5lptog4w FOREIGN KEY (id) REFERENCES public.benutzer(id);


--
-- TOC entry 4997 (class 2606 OID 16768)
-- Name: stunde fkjp84m8cgeak6tquwqbdcuxn8t; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fkjp84m8cgeak6tquwqbdcuxn8t FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 4998 (class 2606 OID 16763)
-- Name: stunde fkmtx1bqtv9orexgbphnhm2hijq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fkmtx1bqtv9orexgbphnhm2hijq FOREIGN KEY (klasse_id) REFERENCES public.klasse(id);


--
-- TOC entry 5015 (class 2606 OID 16931)
-- Name: vertretung fknutvnx7uj90klywyc26wxw419; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.vertretung
    ADD CONSTRAINT fknutvnx7uj90klywyc26wxw419 FOREIGN KEY (vertretungslehrer_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 4999 (class 2606 OID 16758)
-- Name: stunde fko2b7p1bmkd45x6ncc138ooq9j; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fko2b7p1bmkd45x6ncc138ooq9j FOREIGN KEY (fach_id) REFERENCES public.fach(id);


--
-- TOC entry 5007 (class 2606 OID 16819)
-- Name: sperrzeit fkomdce9k8v3eqfgd578ibl2gr9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sperrzeit
    ADD CONSTRAINT fkomdce9k8v3eqfgd578ibl2gr9 FOREIGN KEY (zeitslot_id) REFERENCES public.zeitslot(id);


--
-- TOC entry 5016 (class 2606 OID 16988)
-- Name: schueler fkox1rqxdb1y7c56lgc11896dd2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.schueler
    ADD CONSTRAINT fkox1rqxdb1y7c56lgc11896dd2 FOREIGN KEY (klasse_id) REFERENCES public.klasse(id);


--
-- TOC entry 5017 (class 2606 OID 17008)
-- Name: erziehungsberechtigter fkp32bnvfu9nip5jqa0agmfxs8m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.erziehungsberechtigter
    ADD CONSTRAINT fkp32bnvfu9nip5jqa0agmfxs8m FOREIGN KEY (schueler_id) REFERENCES public.schueler(id);


--
-- TOC entry 5000 (class 2606 OID 16783)
-- Name: stunde fkp7pe9xk49jflic7wk5wmcoflg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fkp7pe9xk49jflic7wk5wmcoflg FOREIGN KEY (zeitslot_id) REFERENCES public.zeitslot(id);


--
-- TOC entry 5001 (class 2606 OID 16778)
-- Name: stunde fkpf6lyv6jvohwome2x207t0au1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fkpf6lyv6jvohwome2x207t0au1 FOREIGN KEY (stundenplan_id) REFERENCES public.stundenplan(id);


--
-- TOC entry 5005 (class 2606 OID 16804)
-- Name: lehrkraft_fach fktg4krx7ubgibkb3qv6w6lqqi7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lehrkraft_fach
    ADD CONSTRAINT fktg4krx7ubgibkb3qv6w6lqqi7 FOREIGN KEY (fach_id) REFERENCES public.fach(id);


--
-- TOC entry 4996 (class 2606 OID 16753)
-- Name: klasse fkthce7ojopm568exuj18xjtvp8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.klasse
    ADD CONSTRAINT fkthce7ojopm568exuj18xjtvp8 FOREIGN KEY (lehrkraft_id) REFERENCES public.lehrkraft(id);


--
-- TOC entry 5002 (class 2606 OID 16773)
-- Name: stunde fkw9gdj19dtqr0nffhdeww3ev0; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.stunde
    ADD CONSTRAINT fkw9gdj19dtqr0nffhdeww3ev0 FOREIGN KEY (raum_id) REFERENCES public.raum(id);


-- Completed on 2026-05-20 17:00:19

--
-- PostgreSQL database dump complete
--


