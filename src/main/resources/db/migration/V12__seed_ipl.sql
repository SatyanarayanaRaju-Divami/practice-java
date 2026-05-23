DO $$
DECLARE
    v_league_id  UUID;
    v_season_id  UUID;
    v_csk_id     UUID;
    v_dc_id      UUID;
    v_gt_id      UUID;
    v_kkr_id     UUID;
    v_lsg_id     UUID;
    v_mi_id      UUID;
    v_pbks_id    UUID;
    v_rr_id      UUID;
    v_rcb_id     UUID;
    v_srh_id     UUID;
BEGIN

-- ─────────────────────────────────────────────
-- LEAGUE
-- ─────────────────────────────────────────────
INSERT INTO leagues (name, description, created_at, updated_at)
VALUES ('Indian Premier League', 'TATA IPL – Board of Control for Cricket in India', now(), now())
RETURNING id INTO v_league_id;

-- ─────────────────────────────────────────────
-- SEASON
-- ─────────────────────────────────────────────
INSERT INTO seasons (league_id, name, status, created_at, updated_at)
VALUES (v_league_id, 'Season 19', 'DRAFT', now(), now())
RETURNING id INTO v_season_id;

-- ─────────────────────────────────────────────
-- TEAMS
-- ─────────────────────────────────────────────
INSERT INTO teams (name, created_at, updated_at) VALUES ('Chennai Super Kings',     now(), now()) RETURNING id INTO v_csk_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Delhi Capitals',           now(), now()) RETURNING id INTO v_dc_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Gujarat Titans',           now(), now()) RETURNING id INTO v_gt_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Kolkata Knight Riders',   now(), now()) RETURNING id INTO v_kkr_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Lucknow Super Giants',    now(), now()) RETURNING id INTO v_lsg_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Mumbai Indians',           now(), now()) RETURNING id INTO v_mi_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Punjab Kings',             now(), now()) RETURNING id INTO v_pbks_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Rajasthan Royals',         now(), now()) RETURNING id INTO v_rr_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Royal Challengers Bengaluru', now(), now()) RETURNING id INTO v_rcb_id;
INSERT INTO teams (name, created_at, updated_at) VALUES ('Sunrisers Hyderabad',     now(), now()) RETURNING id INTO v_srh_id;

-- ─────────────────────────────────────────────
-- PLAYERS – Chennai Super Kings
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_csk_id, 'Ruturaj Gaikwad',    now(), now()),
    (v_csk_id, 'MS Dhoni',           now(), now()),
    (v_csk_id, 'Sanju Samson',       now(), now()),
    (v_csk_id, 'Dewald Brevis',      now(), now()),
    (v_csk_id, 'Ayush Mhatre',       now(), now()),
    (v_csk_id, 'Kartik Sharma',      now(), now()),
    (v_csk_id, 'Sarfaraz Khan',      now(), now()),
    (v_csk_id, 'Urvil Patel',        now(), now()),
    (v_csk_id, 'Jamie Overton',      now(), now()),
    (v_csk_id, 'Ramakrishna Ghosh',  now(), now()),
    (v_csk_id, 'Prashant Veer',      now(), now()),
    (v_csk_id, 'Matthew Short',      now(), now()),
    (v_csk_id, 'Aman Khan',          now(), now()),
    (v_csk_id, 'Zak Foulkes',        now(), now()),
    (v_csk_id, 'Shivam Dube',        now(), now()),
    (v_csk_id, 'Khaleel Ahmed',      now(), now()),
    (v_csk_id, 'Noor Ahmad',         now(), now()),
    (v_csk_id, 'Anshul Kamboj',      now(), now()),
    (v_csk_id, 'Mukesh Choudhary',   now(), now()),
    (v_csk_id, 'Shreyas Gopal',      now(), now()),
    (v_csk_id, 'Gurjapneet Singh',   now(), now()),
    (v_csk_id, 'Akeal Hosein',       now(), now()),
    (v_csk_id, 'Matt Henry',         now(), now()),
    (v_csk_id, 'Rahul Chahar',       now(), now()),
    (v_csk_id, 'Spencer Johnson',    now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Delhi Capitals
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_dc_id, 'KL Rahul',              now(), now()),
    (v_dc_id, 'Karun Nair',            now(), now()),
    (v_dc_id, 'David Miller',          now(), now()),
    (v_dc_id, 'Pathum Nissanka',       now(), now()),
    (v_dc_id, 'Sahil Parakh',          now(), now()),
    (v_dc_id, 'Prithvi Shaw',          now(), now()),
    (v_dc_id, 'Abishek Porel',         now(), now()),
    (v_dc_id, 'Tristan Stubbs',        now(), now()),
    (v_dc_id, 'Axar Patel',            now(), now()),
    (v_dc_id, 'Sameer Rizvi',          now(), now()),
    (v_dc_id, 'Ashutosh Sharma',       now(), now()),
    (v_dc_id, 'Vipraj Nigam',          now(), now()),
    (v_dc_id, 'Ajay Mandal',           now(), now()),
    (v_dc_id, 'Tripurana Vijay',       now(), now()),
    (v_dc_id, 'Madhav Tiwari',         now(), now()),
    (v_dc_id, 'Nitish Rana',           now(), now()),
    (v_dc_id, 'Mitchell Starc',        now(), now()),
    (v_dc_id, 'T. Natarajan',          now(), now()),
    (v_dc_id, 'Mukesh Kumar',          now(), now()),
    (v_dc_id, 'Dushmantha Chameera',   now(), now()),
    (v_dc_id, 'Auqib Nabi',            now(), now()),
    (v_dc_id, 'Lungisani Ngidi',       now(), now()),
    (v_dc_id, 'Kyle Jamieson',         now(), now()),
    (v_dc_id, 'Kuldeep Yadav',         now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Gujarat Titans
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_gt_id, 'Shubman Gill',          now(), now()),
    (v_gt_id, 'Jos Buttler',           now(), now()),
    (v_gt_id, 'Kumar Kushagra',        now(), now()),
    (v_gt_id, 'Anuj Rawat',            now(), now()),
    (v_gt_id, 'Tom Banton',            now(), now()),
    (v_gt_id, 'Glenn Phillips',        now(), now()),
    (v_gt_id, 'Sai Sudharsan',         now(), now()),
    (v_gt_id, 'Nishant Sindhu',        now(), now()),
    (v_gt_id, 'Washington Sundar',     now(), now()),
    (v_gt_id, 'Mohd. Arshad Khan',     now(), now()),
    (v_gt_id, 'Sai Kishore',           now(), now()),
    (v_gt_id, 'Jayant Yadav',          now(), now()),
    (v_gt_id, 'Jason Holder',          now(), now()),
    (v_gt_id, 'Rahul Tewatia',         now(), now()),
    (v_gt_id, 'Shahrukh Khan',         now(), now()),
    (v_gt_id, 'Kagiso Rabada',         now(), now()),
    (v_gt_id, 'Mohammed Siraj',        now(), now()),
    (v_gt_id, 'Prasidh Krishna',       now(), now()),
    (v_gt_id, 'Manav Suthar',          now(), now()),
    (v_gt_id, 'Gurnoor Singh Brar',    now(), now()),
    (v_gt_id, 'Ishant Sharma',         now(), now()),
    (v_gt_id, 'Ashok Sharma',          now(), now()),
    (v_gt_id, 'Luke Wood',             now(), now()),
    (v_gt_id, 'Kulwant Khejroliya',    now(), now()),
    (v_gt_id, 'Rashid Khan',           now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Kolkata Knight Riders
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_kkr_id, 'Ajinkya Rahane',        now(), now()),
    (v_kkr_id, 'Rinku Singh',           now(), now()),
    (v_kkr_id, 'Angkrish Raghuvanshi', now(), now()),
    (v_kkr_id, 'Manish Pandey',         now(), now()),
    (v_kkr_id, 'Finn Allen',            now(), now()),
    (v_kkr_id, 'Tejasvi Singh',         now(), now()),
    (v_kkr_id, 'Rahul Tripathi',        now(), now()),
    (v_kkr_id, 'Tim Seifert',           now(), now()),
    (v_kkr_id, 'Rovman Powell',         now(), now()),
    (v_kkr_id, 'Anukul Roy',            now(), now()),
    (v_kkr_id, 'Cameron Green',         now(), now()),
    (v_kkr_id, 'Sarthak Ranjan',        now(), now()),
    (v_kkr_id, 'Daksh Kamra',           now(), now()),
    (v_kkr_id, 'Rachin Ravindra',       now(), now()),
    (v_kkr_id, 'Ramandeep Singh',       now(), now()),
    (v_kkr_id, 'Sunil Narine',          now(), now()),
    (v_kkr_id, 'Blessing Muzarabani',   now(), now()),
    (v_kkr_id, 'Vaibhav Arora',         now(), now()),
    (v_kkr_id, 'Matheesha Pathirana',   now(), now()),
    (v_kkr_id, 'Kartik Tyagi',          now(), now()),
    (v_kkr_id, 'Prashant Solanki',      now(), now()),
    (v_kkr_id, 'Saurabh Dubey',         now(), now()),
    (v_kkr_id, 'Navdeep Saini',         now(), now()),
    (v_kkr_id, 'Umran Malik',           now(), now()),
    (v_kkr_id, 'Varun Chakaravarthy',   now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Lucknow Super Giants
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_lsg_id, 'Rishabh Pant',          now(), now()),
    (v_lsg_id, 'Aiden Markram',         now(), now()),
    (v_lsg_id, 'Himmat Singh',          now(), now()),
    (v_lsg_id, 'Matthew Breetzke',      now(), now()),
    (v_lsg_id, 'Mukul Choudhary',       now(), now()),
    (v_lsg_id, 'Akshat Raghuwanshi',    now(), now()),
    (v_lsg_id, 'Josh Inglis',           now(), now()),
    (v_lsg_id, 'Nicholas Pooran',       now(), now()),
    (v_lsg_id, 'Mitchell Marsh',        now(), now()),
    (v_lsg_id, 'Abdul Samad',           now(), now()),
    (v_lsg_id, 'Shahbaz Ahamad',        now(), now()),
    (v_lsg_id, 'Arshin Kulkarni',       now(), now()),
    (v_lsg_id, 'Wanindu Hasaranga',     now(), now()),
    (v_lsg_id, 'Ayush Badoni',          now(), now()),
    (v_lsg_id, 'Mohammad Shami',        now(), now()),
    (v_lsg_id, 'Avesh Khan',            now(), now()),
    (v_lsg_id, 'M. Siddharth',          now(), now()),
    (v_lsg_id, 'Digvesh Singh',         now(), now()),
    (v_lsg_id, 'Akash Singh',           now(), now()),
    (v_lsg_id, 'Prince Yadav',          now(), now()),
    (v_lsg_id, 'Arjun Tendulkar',       now(), now()),
    (v_lsg_id, 'Anrich Nortje',         now(), now()),
    (v_lsg_id, 'Naman Tiwari',          now(), now()),
    (v_lsg_id, 'Mayank Yadav',          now(), now()),
    (v_lsg_id, 'Mohsin Khan',           now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Mumbai Indians
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_mi_id, 'Rohit Sharma',          now(), now()),
    (v_mi_id, 'Suryakumar Yadav',      now(), now()),
    (v_mi_id, 'Robin Minz',            now(), now()),
    (v_mi_id, 'Sherfane Rutherford',   now(), now()),
    (v_mi_id, 'Ryan Rickelton',        now(), now()),
    (v_mi_id, 'Quinton de Kock',       now(), now()),
    (v_mi_id, 'Danish Malewar',        now(), now()),
    (v_mi_id, 'N. Tilak Varma',        now(), now()),
    (v_mi_id, 'Hardik Pandya',         now(), now()),
    (v_mi_id, 'Naman Dhir',            now(), now()),
    (v_mi_id, 'Mitchell Santner',      now(), now()),
    (v_mi_id, 'Raj Angad Bawa',        now(), now()),
    (v_mi_id, 'Atharva Ankolekar',     now(), now()),
    (v_mi_id, 'Mayank Rawat',          now(), now()),
    (v_mi_id, 'Corbin Bosch',          now(), now()),
    (v_mi_id, 'Will Jacks',            now(), now()),
    (v_mi_id, 'Shardul Thakur',        now(), now()),
    (v_mi_id, 'Trent Boult',           now(), now()),
    (v_mi_id, 'Mayank Markande',       now(), now()),
    (v_mi_id, 'Deepak Chahar',         now(), now()),
    (v_mi_id, 'Ashwani Kumar',         now(), now()),
    (v_mi_id, 'Raghu Sharma',          now(), now()),
    (v_mi_id, 'Mohammad Izhar',        now(), now()),
    (v_mi_id, 'Allah Ghazanfar',       now(), now()),
    (v_mi_id, 'Jasprit Bumrah',        now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Punjab Kings
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_pbks_id, 'Shreyas Iyer',         now(), now()),
    (v_pbks_id, 'Nehal Wadhera',        now(), now()),
    (v_pbks_id, 'Vishnu Vinod',         now(), now()),
    (v_pbks_id, 'Harnoor Pannu',        now(), now()),
    (v_pbks_id, 'Pyla Avinash',         now(), now()),
    (v_pbks_id, 'Prabhsimran Singh',    now(), now()),
    (v_pbks_id, 'Shashank Singh',       now(), now()),
    (v_pbks_id, 'Marcus Stoinis',       now(), now()),
    (v_pbks_id, 'Harprett Brar',        now(), now()),
    (v_pbks_id, 'Marco Jansen',         now(), now()),
    (v_pbks_id, 'Azmatullah Omarzai',   now(), now()),
    (v_pbks_id, 'Priyansh Arya',        now(), now()),
    (v_pbks_id, 'Musheer Khan',         now(), now()),
    (v_pbks_id, 'Suryansh Shedge',      now(), now()),
    (v_pbks_id, 'Mitch Owen',           now(), now()),
    (v_pbks_id, 'Cooper Connolly',      now(), now()),
    (v_pbks_id, 'Ben Dwarshuis',        now(), now()),
    (v_pbks_id, 'Arshdeep Singh',       now(), now()),
    (v_pbks_id, 'Yuzvendra Chahal',     now(), now()),
    (v_pbks_id, 'Vyshak Vijaykumar',    now(), now()),
    (v_pbks_id, 'Yash Thakur',          now(), now()),
    (v_pbks_id, 'Xavier Bartlett',      now(), now()),
    (v_pbks_id, 'Pravin Dubey',         now(), now()),
    (v_pbks_id, 'Vishal Nishad',        now(), now()),
    (v_pbks_id, 'Lockie Ferguson',      now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Rajasthan Royals
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_rr_id, 'Yashasvi Jaiswal',      now(), now()),
    (v_rr_id, 'Riyan Parag',           now(), now()),
    (v_rr_id, 'Dhruv Jurel',           now(), now()),
    (v_rr_id, 'Shimron Hetmyer',       now(), now()),
    (v_rr_id, 'Shubham Dubey',         now(), now()),
    (v_rr_id, 'Vaibhav Suryavanshi',   now(), now()),
    (v_rr_id, 'Donovan Ferreira',      now(), now()),
    (v_rr_id, 'Lhuan-Dre Pretorious',  now(), now()),
    (v_rr_id, 'Ravi Singh',            now(), now()),
    (v_rr_id, 'Aman Rao Perala',       now(), now()),
    (v_rr_id, 'Yudhvir Singh Charak',  now(), now()),
    (v_rr_id, 'Ravindra Jadeja',       now(), now()),
    (v_rr_id, 'Dasun Shanaka',         now(), now()),
    (v_rr_id, 'Jofra Archer',          now(), now()),
    (v_rr_id, 'Tushar Deshpande',      now(), now()),
    (v_rr_id, 'Kwena Maphaka',         now(), now()),
    (v_rr_id, 'Ravi Bishnoi',          now(), now()),
    (v_rr_id, 'Sushant Mishra',        now(), now()),
    (v_rr_id, 'Yash Raj Punja',        now(), now()),
    (v_rr_id, 'Vignesh Puthur',        now(), now()),
    (v_rr_id, 'Brijesh Sharma',        now(), now()),
    (v_rr_id, 'Adam Milne',            now(), now()),
    (v_rr_id, 'Kuldeep Sen',           now(), now()),
    (v_rr_id, 'Sandeep Sharma',        now(), now()),
    (v_rr_id, 'Nandre Burger',         now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Royal Challengers Bengaluru
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_rcb_id, 'Virat Kohli',          now(), now()),
    (v_rcb_id, 'Rajat Patidar',        now(), now()),
    (v_rcb_id, 'Devdutt Padikkal',     now(), now()),
    (v_rcb_id, 'Phil Salt',            now(), now()),
    (v_rcb_id, 'Jitesh Sharma',        now(), now()),
    (v_rcb_id, 'Jordan Cox',           now(), now()),
    (v_rcb_id, 'Krunal Pandya',        now(), now()),
    (v_rcb_id, 'Swapnil Singh',        now(), now()),
    (v_rcb_id, 'Tim David',            now(), now()),
    (v_rcb_id, 'Romario Shepherd',     now(), now()),
    (v_rcb_id, 'Jacob Bethell',        now(), now()),
    (v_rcb_id, 'Venkatesh Iyer',       now(), now()),
    (v_rcb_id, 'Satvik Deswal',        now(), now()),
    (v_rcb_id, 'Mangesh Yadav',        now(), now()),
    (v_rcb_id, 'Vicky Ostwal',         now(), now()),
    (v_rcb_id, 'Vihaan Malhotra',      now(), now()),
    (v_rcb_id, 'Kanishk Chouhan',      now(), now()),
    (v_rcb_id, 'Josh Hazlewood',       now(), now()),
    (v_rcb_id, 'Rasikh Dar',           now(), now()),
    (v_rcb_id, 'Suyash Sharma',        now(), now()),
    (v_rcb_id, 'Bhuvneshwar Kumar',    now(), now()),
    (v_rcb_id, 'Abhinandan Singh',     now(), now()),
    (v_rcb_id, 'Jacob Duffy',          now(), now()),
    (v_rcb_id, 'Richard Gleeson',      now(), now()),
    (v_rcb_id, 'Yash Dayal',           now(), now());

-- ─────────────────────────────────────────────
-- PLAYERS – Sunrisers Hyderabad
-- ─────────────────────────────────────────────
INSERT INTO players (team_id, name, created_at, updated_at) VALUES
    (v_srh_id, 'Travis Head',          now(), now()),
    (v_srh_id, 'Abhishek Sharma',      now(), now()),
    (v_srh_id, 'Ishan Kishan',         now(), now()),
    (v_srh_id, 'Heinrich Klassen',     now(), now()),
    (v_srh_id, 'Liam Livingstone',     now(), now()),
    (v_srh_id, 'Kamindu Mendis',       now(), now()),
    (v_srh_id, 'Aniket Verma',         now(), now()),
    (v_srh_id, 'Smaran Ravichandran',  now(), now()),
    (v_srh_id, 'Salil Arora',          now(), now()),
    (v_srh_id, 'Harshal Patel',        now(), now()),
    (v_srh_id, 'Harsh Dubey',          now(), now()),
    (v_srh_id, 'Shivang Kumar',        now(), now()),
    (v_srh_id, 'Nitesh Kumar Reddy',   now(), now()),
    (v_srh_id, 'Pat Cummins',          now(), now()),
    (v_srh_id, 'Zeeshan Ansari',       now(), now()),
    (v_srh_id, 'Jaydev Unadkat',       now(), now()),
    (v_srh_id, 'Eshan Malinga',        now(), now()),
    (v_srh_id, 'Sakib Hussain',        now(), now()),
    (v_srh_id, 'Onkar Tarmale',        now(), now()),
    (v_srh_id, 'Amit Kumar',           now(), now()),
    (v_srh_id, 'Praful Hinge',         now(), now()),
    (v_srh_id, 'Dilshan Madushanka',   now(), now()),
    (v_srh_id, 'Gerald Coetzee',       now(), now()),
    (v_srh_id, 'R.S. Ambrish',         now(), now()),
    (v_srh_id, 'Krains Fuletra',       now(), now());

-- ─────────────────────────────────────────────
-- SEASON-TEAM ENROLLMENT (all 10 teams in Season 19)
-- ─────────────────────────────────────────────
INSERT INTO season_teams (season_id, team_id, seed_position, created_at, updated_at) VALUES
    (v_season_id, v_csk_id,  1,  now(), now()),
    (v_season_id, v_dc_id,   2,  now(), now()),
    (v_season_id, v_gt_id,   3,  now(), now()),
    (v_season_id, v_kkr_id,  4,  now(), now()),
    (v_season_id, v_lsg_id,  5,  now(), now()),
    (v_season_id, v_mi_id,   6,  now(), now()),
    (v_season_id, v_pbks_id, 7,  now(), now()),
    (v_season_id, v_rr_id,   8,  now(), now()),
    (v_season_id, v_rcb_id,  9,  now(), now()),
    (v_season_id, v_srh_id,  10, now(), now());

END $$;
