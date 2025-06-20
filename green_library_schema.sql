PGDMP  4    #                }           postgres    16.2    16.2 B               0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    5    postgres    DATABASE     �   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE postgres;
                postgres    false                       0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    4885                       0    0    DATABASE postgres    ACL     ,   GRANT CONNECT ON DATABASE postgres TO alaa;
                   postgres    false    4885                        2615    16399    library    SCHEMA        CREATE SCHEMA library;
    DROP SCHEMA library;
                alaa    false            �            1255    16712    check_library_card()    FUNCTION     %  CREATE FUNCTION library.check_library_card() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Customer WHERE CustID = NEW.CustID AND LibraryCard = true) THEN
        RAISE EXCEPTION 'Customer must have a library card';
    END IF;
    RETURN NEW;
END;
$$;
 ,   DROP FUNCTION library.check_library_card();
       library          alaa    false    7            �            1255    16714    decrease_book_count_on_borrow()    FUNCTION     �  CREATE FUNCTION library.decrease_book_count_on_borrow() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE Book SET BookCount = BookCount - 1 WHERE BookID = NEW.BookID;
    
    -- Update BookAvailable to false when BookCount reaches zero
    IF (SELECT BookCount FROM Book WHERE BookID = NEW.BookID) = 0 THEN
        UPDATE Book SET BookAvailable = false WHERE BookID = NEW.BookID;
    END IF;

    RETURN NEW;
END;
$$;
 7   DROP FUNCTION library.decrease_book_count_on_borrow();
       library          alaa    false    7            �            1255    16716 !   decrease_book_count_on_purchase()    FUNCTION     �  CREATE FUNCTION library.decrease_book_count_on_purchase() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    UPDATE Book SET BookCount = BookCount - 1 WHERE BookID = NEW.BookID;

    -- Update BookAvailable to false when BookCount reaches zero
    IF (SELECT BookCount FROM Book WHERE BookID = NEW.BookID) = 0 THEN
        UPDATE Book SET BookAvailable = false WHERE BookID = NEW.BookID;
    END IF;

    RETURN NEW;
END;
$$;
 9   DROP FUNCTION library.decrease_book_count_on_purchase();
       library          alaa    false    7            �            1259    16611    author    TABLE     �   CREATE TABLE library.author (
    authid integer NOT NULL,
    authfname character varying(20) NOT NULL,
    authlname character varying(20) NOT NULL,
    authcountry character varying(20)
);
    DROP TABLE library.author;
       library         heap    alaa    false    7            �            1259    16610    author_authid_seq    SEQUENCE     �   CREATE SEQUENCE library.author_authid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE library.author_authid_seq;
       library          alaa    false    7    226                       0    0    author_authid_seq    SEQUENCE OWNED BY     I   ALTER SEQUENCE library.author_authid_seq OWNED BY library.author.authid;
          library          alaa    false    225            �            1259    16583    book    TABLE     �   CREATE TABLE library.book (
    bookid integer NOT NULL,
    bookname character varying(50) NOT NULL,
    bookavailable boolean NOT NULL,
    empid integer,
    pubid integer,
    authid integer,
    bookcount integer
);
    DROP TABLE library.book;
       library         heap    alaa    false    7            �            1259    16582    book_bookid_seq    SEQUENCE     �   CREATE SEQUENCE library.book_bookid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE library.book_bookid_seq;
       library          alaa    false    218    7                       0    0    book_bookid_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE library.book_bookid_seq OWNED BY library.book.bookid;
          library          alaa    false    217            �            1259    16627    borrows    TABLE     �   CREATE TABLE library.borrows (
    custid integer NOT NULL,
    bookid integer NOT NULL,
    startdate date,
    enddate date GENERATED ALWAYS AS ((startdate + 10)) STORED
);
    DROP TABLE library.borrows;
       library         heap    alaa    false    7            �            1259    16597    customer    TABLE     �   CREATE TABLE library.customer (
    custid integer NOT NULL,
    custfname character varying(20) NOT NULL,
    custlname character varying(20) NOT NULL,
    librarycard boolean,
    custcontact character varying(20),
    password integer
);
    DROP TABLE library.customer;
       library         heap    alaa    false    7            �            1259    16596    customer_custid_seq    SEQUENCE     �   CREATE SEQUENCE library.customer_custid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE library.customer_custid_seq;
       library          alaa    false    222    7                       0    0    customer_custid_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE library.customer_custid_seq OWNED BY library.customer.custid;
          library          alaa    false    221            �            1259    16590    employee    TABLE     �   CREATE TABLE library.employee (
    empid integer NOT NULL,
    empfname character varying(20) NOT NULL,
    emplname character varying(20) NOT NULL,
    empsalary double precision,
    password integer
);
    DROP TABLE library.employee;
       library         heap    alaa    false    7            �            1259    16589    employee_empid_seq    SEQUENCE     �   CREATE SEQUENCE library.employee_empid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 *   DROP SEQUENCE library.employee_empid_seq;
       library          alaa    false    220    7                       0    0    employee_empid_seq    SEQUENCE OWNED BY     K   ALTER SEQUENCE library.employee_empid_seq OWNED BY library.employee.empid;
          library          alaa    false    219            �            1259    16617    feedback    TABLE     �   CREATE TABLE library.feedback (
    description character varying(100) NOT NULL,
    rating_10 integer NOT NULL,
    custid integer
);
    DROP TABLE library.feedback;
       library         heap    alaa    false    7            �            1259    16604 	   publisher    TABLE     �   CREATE TABLE library.publisher (
    pubid integer NOT NULL,
    pubname character varying(40) NOT NULL,
    pubcountry character varying(20)
);
    DROP TABLE library.publisher;
       library         heap    alaa    false    7            �            1259    16603    publisher_pubid_seq    SEQUENCE     �   CREATE SEQUENCE library.publisher_pubid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE library.publisher_pubid_seq;
       library          alaa    false    224    7                       0    0    publisher_pubid_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE library.publisher_pubid_seq OWNED BY library.publisher.pubid;
          library          alaa    false    223            �            1259    16622 	   purchases    TABLE     ]   CREATE TABLE library.purchases (
    custid integer NOT NULL,
    bookid integer NOT NULL
);
    DROP TABLE library.purchases;
       library         heap    alaa    false    7            V           2604    16614    author authid    DEFAULT     p   ALTER TABLE ONLY library.author ALTER COLUMN authid SET DEFAULT nextval('library.author_authid_seq'::regclass);
 =   ALTER TABLE library.author ALTER COLUMN authid DROP DEFAULT;
       library          alaa    false    226    225    226            R           2604    16586    book bookid    DEFAULT     l   ALTER TABLE ONLY library.book ALTER COLUMN bookid SET DEFAULT nextval('library.book_bookid_seq'::regclass);
 ;   ALTER TABLE library.book ALTER COLUMN bookid DROP DEFAULT;
       library          alaa    false    217    218    218            T           2604    16600    customer custid    DEFAULT     t   ALTER TABLE ONLY library.customer ALTER COLUMN custid SET DEFAULT nextval('library.customer_custid_seq'::regclass);
 ?   ALTER TABLE library.customer ALTER COLUMN custid DROP DEFAULT;
       library          alaa    false    221    222    222            S           2604    16593    employee empid    DEFAULT     r   ALTER TABLE ONLY library.employee ALTER COLUMN empid SET DEFAULT nextval('library.employee_empid_seq'::regclass);
 >   ALTER TABLE library.employee ALTER COLUMN empid DROP DEFAULT;
       library          alaa    false    220    219    220            U           2604    16607    publisher pubid    DEFAULT     t   ALTER TABLE ONLY library.publisher ALTER COLUMN pubid SET DEFAULT nextval('library.publisher_pubid_seq'::regclass);
 ?   ALTER TABLE library.publisher ALTER COLUMN pubid DROP DEFAULT;
       library          alaa    false    224    223    224                      0    16611    author 
   TABLE DATA           L   COPY library.author (authid, authfname, authlname, authcountry) FROM stdin;
    library          alaa    false    226   :O                 0    16583    book 
   TABLE DATA           a   COPY library.book (bookid, bookname, bookavailable, empid, pubid, authid, bookcount) FROM stdin;
    library          alaa    false    218   ;P                 0    16627    borrows 
   TABLE DATA           =   COPY library.borrows (custid, bookid, startdate) FROM stdin;
    library          alaa    false    229   KR                 0    16597    customer 
   TABLE DATA           e   COPY library.customer (custid, custfname, custlname, librarycard, custcontact, password) FROM stdin;
    library          alaa    false    222   �R                 0    16590    employee 
   TABLE DATA           S   COPY library.employee (empid, empfname, emplname, empsalary, password) FROM stdin;
    library          alaa    false    220   �S                 0    16617    feedback 
   TABLE DATA           C   COPY library.feedback (description, rating_10, custid) FROM stdin;
    library          alaa    false    227   'T       
          0    16604 	   publisher 
   TABLE DATA           @   COPY library.publisher (pubid, pubname, pubcountry) FROM stdin;
    library          alaa    false    224   �T                 0    16622 	   purchases 
   TABLE DATA           4   COPY library.purchases (custid, bookid) FROM stdin;
    library          alaa    false    228   �U                  0    0    author_authid_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('library.author_authid_seq', 1, false);
          library          alaa    false    225                       0    0    book_bookid_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('library.book_bookid_seq', 14, true);
          library          alaa    false    217                       0    0    customer_custid_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('library.customer_custid_seq', 1, false);
          library          alaa    false    221                        0    0    employee_empid_seq    SEQUENCE SET     A   SELECT pg_catalog.setval('library.employee_empid_seq', 9, true);
          library          alaa    false    219            !           0    0    publisher_pubid_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('library.publisher_pubid_seq', 1, false);
          library          alaa    false    223            a           2606    16616    author author_pkey 
   CONSTRAINT     U   ALTER TABLE ONLY library.author
    ADD CONSTRAINT author_pkey PRIMARY KEY (authid);
 =   ALTER TABLE ONLY library.author DROP CONSTRAINT author_pkey;
       library            alaa    false    226            Y           2606    16588    book book_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY library.book
    ADD CONSTRAINT book_pkey PRIMARY KEY (bookid);
 9   ALTER TABLE ONLY library.book DROP CONSTRAINT book_pkey;
       library            alaa    false    218            g           2606    16631    borrows borrows_pkey 
   CONSTRAINT     _   ALTER TABLE ONLY library.borrows
    ADD CONSTRAINT borrows_pkey PRIMARY KEY (custid, bookid);
 ?   ALTER TABLE ONLY library.borrows DROP CONSTRAINT borrows_pkey;
       library            alaa    false    229    229            ]           2606    16602    customer customer_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY library.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (custid);
 A   ALTER TABLE ONLY library.customer DROP CONSTRAINT customer_pkey;
       library            alaa    false    222            [           2606    16595    employee employee_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY library.employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (empid);
 A   ALTER TABLE ONLY library.employee DROP CONSTRAINT employee_pkey;
       library            alaa    false    220            c           2606    16621    feedback feedback_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY library.feedback
    ADD CONSTRAINT feedback_pkey PRIMARY KEY (description);
 A   ALTER TABLE ONLY library.feedback DROP CONSTRAINT feedback_pkey;
       library            alaa    false    227            _           2606    16609    publisher publisher_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY library.publisher
    ADD CONSTRAINT publisher_pkey PRIMARY KEY (pubid);
 C   ALTER TABLE ONLY library.publisher DROP CONSTRAINT publisher_pkey;
       library            alaa    false    224            e           2606    16626    purchases purchases_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY library.purchases
    ADD CONSTRAINT purchases_pkey PRIMARY KEY (custid, bookid);
 C   ALTER TABLE ONLY library.purchases DROP CONSTRAINT purchases_pkey;
       library            alaa    false    228    228            r           2620    16713    borrows trg_check_library_card    TRIGGER     �   CREATE TRIGGER trg_check_library_card BEFORE INSERT OR UPDATE ON library.borrows FOR EACH ROW EXECUTE FUNCTION library.check_library_card();
 8   DROP TRIGGER trg_check_library_card ON library.borrows;
       library          alaa    false    237    229            s           2620    16718 )   borrows trg_decrease_book_count_on_borrow    TRIGGER     �   CREATE TRIGGER trg_decrease_book_count_on_borrow AFTER INSERT ON library.borrows FOR EACH ROW EXECUTE FUNCTION library.decrease_book_count_on_borrow();
 C   DROP TRIGGER trg_decrease_book_count_on_borrow ON library.borrows;
       library          alaa    false    238    229            q           2620    16719 -   purchases trg_decrease_book_count_on_purchase    TRIGGER     �   CREATE TRIGGER trg_decrease_book_count_on_purchase AFTER INSERT ON library.purchases FOR EACH ROW EXECUTE FUNCTION library.decrease_book_count_on_purchase();
 G   DROP TRIGGER trg_decrease_book_count_on_purchase ON library.purchases;
       library          alaa    false    228    239            h           2606    16637    book book_authid_fkey    FK CONSTRAINT     z   ALTER TABLE ONLY library.book
    ADD CONSTRAINT book_authid_fkey FOREIGN KEY (authid) REFERENCES library.author(authid);
 @   ALTER TABLE ONLY library.book DROP CONSTRAINT book_authid_fkey;
       library          alaa    false    226    4705    218            i           2606    16632    book book_empid_fkey    FK CONSTRAINT     y   ALTER TABLE ONLY library.book
    ADD CONSTRAINT book_empid_fkey FOREIGN KEY (empid) REFERENCES library.employee(empid);
 ?   ALTER TABLE ONLY library.book DROP CONSTRAINT book_empid_fkey;
       library          alaa    false    218    4699    220            j           2606    16642    book book_pubid_fkey    FK CONSTRAINT     z   ALTER TABLE ONLY library.book
    ADD CONSTRAINT book_pubid_fkey FOREIGN KEY (pubid) REFERENCES library.publisher(pubid);
 ?   ALTER TABLE ONLY library.book DROP CONSTRAINT book_pubid_fkey;
       library          alaa    false    4703    218    224            n           2606    16687    borrows borrows_bookid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY library.borrows
    ADD CONSTRAINT borrows_bookid_fkey FOREIGN KEY (bookid) REFERENCES library.book(bookid) ON UPDATE CASCADE ON DELETE CASCADE;
 F   ALTER TABLE ONLY library.borrows DROP CONSTRAINT borrows_bookid_fkey;
       library          alaa    false    218    229    4697            o           2606    16682    borrows borrows_custid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY library.borrows
    ADD CONSTRAINT borrows_custid_fkey FOREIGN KEY (custid) REFERENCES library.customer(custid) ON UPDATE CASCADE ON DELETE CASCADE;
 F   ALTER TABLE ONLY library.borrows DROP CONSTRAINT borrows_custid_fkey;
       library          alaa    false    4701    229    222            k           2606    16652    feedback feedback_custid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY library.feedback
    ADD CONSTRAINT feedback_custid_fkey FOREIGN KEY (custid) REFERENCES library.customer(custid);
 H   ALTER TABLE ONLY library.feedback DROP CONSTRAINT feedback_custid_fkey;
       library          alaa    false    222    4701    227            p           2606    16707    borrows fk_borrows_customer    FK CONSTRAINT     �   ALTER TABLE ONLY library.borrows
    ADD CONSTRAINT fk_borrows_customer FOREIGN KEY (custid) REFERENCES library.customer(custid) ON UPDATE CASCADE ON DELETE CASCADE;
 F   ALTER TABLE ONLY library.borrows DROP CONSTRAINT fk_borrows_customer;
       library          alaa    false    229    4701    222            l           2606    16697    purchases purchases_bookid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY library.purchases
    ADD CONSTRAINT purchases_bookid_fkey FOREIGN KEY (bookid) REFERENCES library.book(bookid) ON UPDATE CASCADE ON DELETE CASCADE;
 J   ALTER TABLE ONLY library.purchases DROP CONSTRAINT purchases_bookid_fkey;
       library          alaa    false    218    228    4697            m           2606    16692    purchases purchases_custid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY library.purchases
    ADD CONSTRAINT purchases_custid_fkey FOREIGN KEY (custid) REFERENCES library.customer(custid) ON UPDATE CASCADE ON DELETE CASCADE;
 J   ALTER TABLE ONLY library.purchases DROP CONSTRAINT purchases_custid_fkey;
       library          alaa    false    4701    222    228               �   x�M��n�0Eח�����DM�'mTe��M0`ald�J��kG��휹g��i��Pa'<q�K}I��9�ܑ{�j�W+u�M a+�(�Ƒ:��x��5ީ�PKl����>��II�:��V`�ۋ��|��MOl-[>`3��X�����u���wN2�[|�Ey@ɮ1��u;�S$�x���D�-{�_��
c�����2���v��*�$�Z��e( �,Qة��Y]��,�$�i?            x�u�ao�0�?�¿`��Ⱦ1����T�Z��.��ĕ�t���..���""?�{��.��Jض譫b�����E�5�N�Fܸ�C %e��dR�0�2�^�\�mv'a:������|w�qו����E����Z��#A��F<#�<��Djӽ���
TN�T0^w{|3����r!�t{����M�+���,[cw;�i�f�bA:OI��i��Ú@J�}q��b'g,�X	w���#���J�`+}��;���q	��Q<+�uc��Z�$ܲ�;�>�Hkp��sk�{�oC����)�QG6��`�0� (���݉o^��?s��8U��nZl��i���3[E�%<�Aۄ6�x�h��n�Ðp��Q[����{c5.��c,���c�U�qG�(r���d�F*�3v^�����z����۶Ơ�ο��7}BVL� g�j
�HRN��it�S���3W�Z׿1���p;�Rs�1X3�b����w{�k�6�_ֺ�b���d2����p         v   x�m�K
�0�us�5�az�s4	��Y=?1"J�z���f?��{�ND`�h�r!�J8J��L��ȁ�Ļ�x�qa�#��E|��>�B�P�A�=r�����>�����Z� ��H�         �   x�-��
�@���_"ٶY�ǂ� ՃW/Q*�W��o��¼��mTE��i~?�K�����U]�)�Y�Mϡ�A��t��|�%f��L���ᨩ�G��ygɨ�d1m��yɮ�yԄN_�i��V@l U�2�p��wP�5�m<�
��p=e�ю	������P� K��(�?oM7V         �   x�5�=�0��ˏ�7�ձ`�vq�k ����&E���8��6o]�=���Z���8ǜ�Mɾ���;��&�ҦS�q��P0�	���p,�B����1l�ǅ�����
��O�Rm�C6�����c�%�,T         �   x�M�;�0Dk���.
�8A@K�����#���ɇ�n��f4m&T�R��`��R�S,��4U���}�)t<c���4 ���/M�X��q�!��.�;Y-�08>�K�0�E?Z��K&��]�q 	3A�Q~������o�mFI�4[����/Ҕ@n      
   �   x�E�;o�P�g�Wx�TU��9(�*TQb��$.X����7B��7A ��s����d-N�s��>�$K�1��j�9R,��Iز��u���0w��TKR����uӉ�;i�\�.�Y��x�31������Q�c��w��9�qh?��[�����B��౏g;��O�я`nT
�z�����LH#5X���(=�^S$���(��\t�֚h�ؽh-4�1̩d�:֓^K���5N��s��?6-��x��M�$�@�l�         N   x�U���@�o��;o��C����^Ҕ�,	�)k�T��%����%ˆQZ���˃鬵����W��s���S�     