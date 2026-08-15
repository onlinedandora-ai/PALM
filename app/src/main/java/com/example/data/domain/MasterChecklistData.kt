package com.example.data.domain

data class MasterChecklistItem(
    val id: String,
    val title: String,
    val subcategory: String,
    val frequency: String, // "Monthly", "Annual", "Occasional", "Seasonal", "Term-wise"
    val estimatedAmount: Double,
    val description: String,
    val defaultType: String, // "Budget", "Subscription", "Expense"
    val examples: List<String>
)

data class MasterChecklistCategory(
    val id: String,
    val categoryNumber: Int,
    val name: String,
    val iconName: String,
    val defaultFrequency: String,
    val items: List<MasterChecklistItem>
)

object MasterChecklistCatalog {

    val categories: List<MasterChecklistCategory> = listOf(
        // 1. Digital & Media Subscriptions
        MasterChecklistCategory(
            id = "digital-subscriptions",
            categoryNumber = 1,
            name = "Subscriptions (Digital & Media)",
            iconName = "tv",
            defaultFrequency = "Monthly / Annual",
            items = listOf(
                MasterChecklistItem(
                    id = "ott-video",
                    title = "OTT & Video Streaming",
                    subcategory = "Media Streaming",
                    frequency = "Monthly",
                    estimatedAmount = 649.0,
                    description = "Video streaming subscriptions for movies, shows, and live TV.",
                    defaultType = "Subscription",
                    examples = listOf("Netflix", "Amazon Prime", "Disney+", "Hulu", "HBO Max", "Apple TV+", "Paramount+", "Peacock")
                ),
                MasterChecklistItem(
                    id = "audio-music",
                    title = "Audio & Music Streaming",
                    subcategory = "Media Streaming",
                    frequency = "Monthly",
                    estimatedAmount = 179.0,
                    description = "Music, podcasts, and audiobook streaming subscriptions.",
                    defaultType = "Subscription",
                    examples = listOf("Spotify", "Apple Music", "YouTube Premium", "Amazon Music", "Audible", "Podcasts", "Pandora", "Tidal")
                ),
                MasterChecklistItem(
                    id = "cloud-software",
                    title = "Software & Cloud Storage",
                    subcategory = "SaaS & Utilities",
                    frequency = "Monthly",
                    estimatedAmount = 219.0,
                    description = "Cloud backup storage, office suites, antivirus security, and VPN services.",
                    defaultType = "Subscription",
                    examples = listOf("Google One", "Apple iCloud", "Microsoft 365", "Dropbox", "Antivirus", "VPN", "NordVPN", "ExpressVPN", "1Password")
                ),
                MasterChecklistItem(
                    id = "news-learning",
                    title = "News, Books & Learning",
                    subcategory = "Publications & Skills",
                    frequency = "Monthly",
                    estimatedAmount = 399.0,
                    description = "Digital newspapers, article publishing platforms, e-books, and language learning apps.",
                    defaultType = "Subscription",
                    examples = listOf("E-newspapers", "Medium", "Kindle Unlimited", "Substack", "Duolingo", "Magazines", "New York Times", "Wall Street Journal")
                ),
                MasterChecklistItem(
                    id = "gaming-apps",
                    title = "Gaming & Apps",
                    subcategory = "Gaming",
                    frequency = "Monthly",
                    estimatedAmount = 499.0,
                    description = "Console gaming passes, online multiplayer memberships, PC game stores, and mobile apps.",
                    defaultType = "Subscription",
                    examples = listOf("PlayStation Plus", "Xbox Game Pass", "Nintendo Switch Online", "Steam", "Discord Nitro", "Mobile app subscriptions")
                )
            )
        ),

        // 2. Insurance Policies
        MasterChecklistCategory(
            id = "insurance-policies",
            categoryNumber = 2,
            name = "Insurance Policies",
            iconName = "shield",
            defaultFrequency = "Monthly / Annual",
            items = listOf(
                MasterChecklistItem(
                    id = "health-insurance",
                    title = "Health Insurance",
                    subcategory = "Health Protection",
                    frequency = "Annual",
                    estimatedAmount = 25000.0,
                    description = "Individual healthcare, family floater policies, critical illness cover, and top-up insurance.",
                    defaultType = "Expense",
                    examples = listOf("Individual cover", "Family floater", "Critical illness cover", "Top-up policies", "Mediclaim")
                ),
                MasterChecklistItem(
                    id = "life-insurance",
                    title = "Life Insurance",
                    subcategory = "Life Protection",
                    frequency = "Annual",
                    estimatedAmount = 18000.0,
                    description = "Term life insurance policies, endowment savings plans, and whole-life coverage.",
                    defaultType = "Expense",
                    examples = listOf("Term life insurance", "Endowment plan", "Whole-life policy")
                ),
                MasterChecklistItem(
                    id = "vehicle-insurance",
                    title = "Vehicle Insurance",
                    subcategory = "Auto Protection",
                    frequency = "Annual",
                    estimatedAmount = 15000.0,
                    description = "Comprehensive car insurance, motorcycle insurance, or EV battery and vehicle premiums.",
                    defaultType = "Subscription",
                    examples = listOf("Comprehensive car insurance", "Bike insurance", "EV insurance premium")
                ),
                MasterChecklistItem(
                    id = "home-insurance",
                    title = "Property & Home Insurance",
                    subcategory = "Asset Protection",
                    frequency = "Annual",
                    estimatedAmount = 8000.0,
                    description = "Renters insurance, structural home insurance, theft, and natural hazard coverage.",
                    defaultType = "Expense",
                    examples = listOf("Renters insurance", "Structure insurance", "Theft coverage", "Hazard coverage")
                ),
                MasterChecklistItem(
                    id = "specialized-insurance",
                    title = "Specialized Insurance",
                    subcategory = "Special Coverage",
                    frequency = "Annual",
                    estimatedAmount = 5000.0,
                    description = "Disability insurance, international travel insurance, and pet health policies.",
                    defaultType = "Expense",
                    examples = listOf("Disability insurance", "Travel insurance", "Pet insurance")
                )
            )
        ),

        // 3. Household Operations & Utilities
        MasterChecklistCategory(
            id = "household-operations",
            categoryNumber = 3,
            name = "Household Operations & Utilities",
            iconName = "home",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "groceries-essentials",
                    title = "Groceries & Essentials",
                    subcategory = "Food & Pantry",
                    frequency = "Monthly",
                    estimatedAmount = 15000.0,
                    description = "Fresh produce, vegetables, daily food items, toiletries, pantry staples, and household cleaning supplies.",
                    defaultType = "Budget",
                    examples = listOf("Fresh produce", "Daily food items", "Toiletries", "Pantry staples", "Cleaning supplies", "Supermarket")
                ),
                MasterChecklistItem(
                    id = "domestic-help",
                    title = "Domestic Help & Services",
                    subcategory = "Staff & Assistance",
                    frequency = "Monthly",
                    estimatedAmount = 8000.0,
                    description = "Salaries and tips for housekeepers, maids, cooks, drivers, security guards, or babysitters.",
                    defaultType = "Budget",
                    examples = listOf("Housekeeper salary", "Cook fee", "Driver pay", "Security guard tips", "Babysitter pay")
                ),
                MasterChecklistItem(
                    id = "utilities-bills",
                    title = "Utilities & Connectivity",
                    subcategory = "Public Services",
                    frequency = "Monthly",
                    estimatedAmount = 4500.0,
                    description = "Electricity bills, municipal water tax, piped gas, LPG cooking cylinders, fiber broadband internet, and mobile phone bills.",
                    defaultType = "Budget",
                    examples = listOf("Electricity", "Municipal water", "Piped gas", "Cooking cylinder", "LPG", "Broadband fiber internet", "Mobile phone bills")
                ),
                MasterChecklistItem(
                    id = "maintenance-repairs",
                    title = "Maintenance & Repairs",
                    subcategory = "Home Upkeep",
                    frequency = "Occasional",
                    estimatedAmount = 5000.0,
                    description = "Plumbing repairs, electrical fixes, HVAC and AC servicing, RO water purifier maintenance, and appliance repairs.",
                    defaultType = "Budget",
                    examples = listOf("Plumbing", "Electrical fixes", "HVAC / AC servicing", "Water purifier RO maintenance", "Appliance repairs")
                ),
                MasterChecklistItem(
                    id = "property-taxes-hoa",
                    title = "Property Taxes & HOA",
                    subcategory = "Housing Dues",
                    frequency = "Annual / Monthly",
                    estimatedAmount = 6000.0,
                    description = "Municipal property taxes, housing society maintenance fees, and neighborhood security charges.",
                    defaultType = "Budget",
                    examples = listOf("Municipal property taxes", "Housing society maintenance fees", "HOA dues", "Neighborhood security fees")
                )
            )
        ),

        // 4. Financial & Asset Management
        MasterChecklistCategory(
            id = "financial-wealth",
            categoryNumber = 4,
            name = "Financial & Asset Management",
            iconName = "bank",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "housing-costs",
                    title = "Housing Costs (Rent / EMI)",
                    subcategory = "Housing Liabilities",
                    frequency = "Monthly",
                    estimatedAmount = 35000.0,
                    description = "Monthly apartment rent payments or Home Loan EMIs including principal and interest.",
                    defaultType = "Budget",
                    examples = listOf("Rent payments", "Home Loan EMI", "Mortgage payment", "Principal & Interest")
                ),
                MasterChecklistItem(
                    id = "vehicle-commute",
                    title = "Vehicle & Commute Expenses",
                    subcategory = "Transportation",
                    frequency = "Monthly",
                    estimatedAmount = 8500.0,
                    description = "Fuel (Petrol, Diesel, EV charging), toll passes (FASTag, E-Pass), parking fees, and scheduled vehicle servicing or oil changes.",
                    defaultType = "Budget",
                    examples = listOf("Fuel", "Petrol", "Diesel", "EV charging", "FASTag", "E-Pass", "Toll passes", "Parking fees", "Vehicle servicing", "Oil changes")
                ),
                MasterChecklistItem(
                    id = "investments-sip",
                    title = "Investments & Wealth Building",
                    subcategory = "Wealth Accumulation",
                    frequency = "Monthly",
                    estimatedAmount = 20000.0,
                    description = "Mutual Fund SIPs, equity stocks, index funds, Recurring Deposits (RD), and government savings bonds.",
                    defaultType = "Budget",
                    examples = listOf("Mutual Fund SIPs", "Stocks", "Index funds", "Recurring Deposits", "RD", "Government savings bonds")
                ),
                MasterChecklistItem(
                    id = "retirement-funds",
                    title = "Retirement Funds",
                    subcategory = "Long-Term Security",
                    frequency = "Monthly",
                    estimatedAmount = 10000.0,
                    description = "Pension plans, 401(k), IRA, Provident Fund (EPF/PPF), or annuity contributions.",
                    defaultType = "Budget",
                    examples = listOf("Pension plans", "401(k)", "IRA", "Provident Fund", "EPF", "PPF", "Annuity contributions")
                )
            )
        ),

        // 5. Health & Education
        MasterChecklistCategory(
            id = "health-education",
            categoryNumber = 5,
            name = "Health & Education",
            iconName = "health",
            defaultFrequency = "Monthly / Term-wise",
            items = listOf(
                MasterChecklistItem(
                    id = "education-tuition",
                    title = "Education & Skill Building",
                    subcategory = "Learning",
                    frequency = "Term-wise",
                    estimatedAmount = 12000.0,
                    description = "School and college tuition fees, online learning courses (Coursera, Udemy), private tutoring, school books, and educational supplies.",
                    defaultType = "Budget",
                    examples = listOf("School tuition fees", "College tuition", "Online courses", "Coursera", "Udemy", "Private tutoring", "School books & supplies")
                ),
                MasterChecklistItem(
                    id = "healthcare-pharmacy",
                    title = "Healthcare & Pharmacy",
                    subcategory = "Medical Care",
                    frequency = "Monthly",
                    estimatedAmount = 4500.0,
                    description = "Doctor checkups, specialist consultations, monthly prescription medicines, vitamins, dietary supplements, dental care, and vision care.",
                    defaultType = "Budget",
                    examples = listOf("Doctor checkups", "Specialist consultations", "Prescription medicines", "Vitamins & supplements", "Dental care", "Vision care", "Glasses")
                ),
                MasterChecklistItem(
                    id = "fitness-wellness",
                    title = "Fitness & Wellness",
                    subcategory = "Wellness",
                    frequency = "Monthly",
                    estimatedAmount = 3000.0,
                    description = "Gym memberships, sports club fees, yoga classes, wellness retreats, and mental health or therapy sessions.",
                    defaultType = "Subscription",
                    examples = listOf("Gym memberships", "Sports club fees", "Yoga classes", "Wellness retreats", "Mental health", "Therapy sessions")
                )
            )
        ),

        // 6. Leisure, Personal & Lifestyle
        MasterChecklistCategory(
            id = "lifestyle-recreation",
            categoryNumber = 6,
            name = "Leisure, Personal & Lifestyle",
            iconName = "lifestyle",
            defaultFrequency = "Monthly / Occasional",
            items = listOf(
                MasterChecklistItem(
                    id = "entertainment-dining",
                    title = "Entertainment & Dining Out",
                    subcategory = "Leisure",
                    frequency = "Monthly",
                    estimatedAmount = 6000.0,
                    description = "Movie tickets, dining out at restaurants, weekend outings, and online food delivery (UberEats, DoorDash, Zomato, Swiggy).",
                    defaultType = "Budget",
                    examples = listOf("Movie tickets", "Dining out", "Weekend outings", "Online food delivery", "UberEats", "DoorDash", "Zomato", "Swiggy")
                ),
                MasterChecklistItem(
                    id = "grooming-personal-care",
                    title = "Personal Care & Grooming",
                    subcategory = "Personal Hygiene",
                    frequency = "Monthly",
                    estimatedAmount = 2500.0,
                    description = "Salon appointments, spa visits, skincare products, cosmetics, barbershop visits, and personal hygiene products.",
                    defaultType = "Budget",
                    examples = listOf("Salon appointments", "Spa visits", "Skincare", "Cosmetics", "Barbershop", "Personal hygiene products")
                ),
                MasterChecklistItem(
                    id = "travel-vacations",
                    title = "Travel & Vacations",
                    subcategory = "Vacation Reserve",
                    frequency = "Occasional",
                    estimatedAmount = 15000.0,
                    description = "Flight and train tickets, hotel stays, vacation savings fund, luggage, and visa processing fees.",
                    defaultType = "Budget",
                    examples = listOf("Flight tickets", "Train tickets", "Hotel stays", "Vacation savings fund", "Luggage", "Visa fees")
                ),
                MasterChecklistItem(
                    id = "hobbies-recreation",
                    title = "Hobbies & Recreation",
                    subcategory = "Interests",
                    frequency = "Occasional",
                    estimatedAmount = 3500.0,
                    description = "Art supplies, musical instrument maintenance, gaming hardware, outdoor gear, craft materials, sewing, knitting, gardening, and DIY tools.",
                    defaultType = "Budget",
                    examples = listOf("Art supplies", "Musical instrument maintenance", "Gaming hardware", "Outdoor gear", "Craft materials", "Sewing", "Knitting", "Gardening", "DIY tools")
                ),
                MasterChecklistItem(
                    id = "sports-country-clubs",
                    title = "Sports & Country Clubs",
                    subcategory = "Recreational Clubs",
                    frequency = "Monthly / Annual",
                    estimatedAmount = 4500.0,
                    description = "Golf course memberships, tennis club dues, community pool access, and recreational sports league fees.",
                    defaultType = "Subscription",
                    examples = listOf("Golf course memberships", "Tennis club dues", "Community pool access", "Recreational league fees")
                )
            )
        ),

        // 7. Childcare & Dependent Care
        MasterChecklistCategory(
            id = "childcare-dependent",
            categoryNumber = 7,
            name = "Childcare & Dependent Care",
            iconName = "child",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "early-childcare",
                    title = "Daycare & Early Education",
                    subcategory = "Early Education",
                    frequency = "Monthly",
                    estimatedAmount = 10000.0,
                    description = "Daycare fees, crèche, preschool tuition, nanny or babysitter pay.",
                    defaultType = "Budget",
                    examples = listOf("Daycare fees", "Crèche", "Preschool tuition", "Nanny pay", "Babysitter pay")
                ),
                MasterChecklistItem(
                    id = "infant-essentials",
                    title = "Baby & Toddler Essentials",
                    subcategory = "Infant Care",
                    frequency = "Monthly",
                    estimatedAmount = 5000.0,
                    description = "Diapers, baby wipes, baby formula, specialized baby food, formula, and sterilization equipment.",
                    defaultType = "Budget",
                    examples = listOf("Diapers", "Baby wipes", "Baby formula", "Specialized baby food", "Sterilization equipment")
                ),
                MasterChecklistItem(
                    id = "child-gear-clothing",
                    title = "Child Gear & Apparel",
                    subcategory = "Kids Wardrobe & Gear",
                    frequency = "Occasional",
                    estimatedAmount = 4000.0,
                    description = "Strollers, car seats, cribs, rapidly outgrown clothing, toys, and baby monitors.",
                    defaultType = "Budget",
                    examples = listOf("Strollers", "Car seats", "Cribs", "Clothing outgrown rapidly", "Toys", "Baby monitors")
                ),
                MasterChecklistItem(
                    id = "extracurricular-kids",
                    title = "Extracurricular Activities & Pocket Money",
                    subcategory = "Kids Activities",
                    frequency = "Monthly",
                    estimatedAmount = 4500.0,
                    description = "Sports leagues coaching, music/dance lessons, art classes, coding bootcamps, summer camps, and kids monthly allowances or pocket money cards.",
                    defaultType = "Budget",
                    examples = listOf("Sports coaching", "Music / Dance lessons", "Art classes", "Coding bootcamps", "Summer camps", "Pocket money", "Allowances")
                )
            )
        ),

        // 8. Pet Care & Expenses
        MasterChecklistCategory(
            id = "pet-care",
            categoryNumber = 8,
            name = "Pet Care & Expenses",
            iconName = "pets",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "pet-food-treats",
                    title = "Pet Food & Nutrition",
                    subcategory = "Pet Food",
                    frequency = "Monthly",
                    estimatedAmount = 3500.0,
                    description = "Regular dry/wet pet food, specialized dietary food, dental treats, and nutritional supplements.",
                    defaultType = "Budget",
                    examples = listOf("Regular pet food", "Specialized diet food", "Dental treats", "Supplements", "Dry food", "Wet food")
                ),
                MasterChecklistItem(
                    id = "vet-care-health",
                    title = "Veterinary & Healthcare",
                    subcategory = "Pet Medical",
                    frequency = "Annual / Occasional",
                    estimatedAmount = 6000.0,
                    description = "Routine vet checkups, annual vaccinations, flea/tick/worming treatments, spay/neuter surgery, emergency vet care, and medication.",
                    defaultType = "Budget",
                    examples = listOf("Routine checkups", "Annual vaccinations", "Flea / Tick / Worming", "Spay / Neuter", "Emergency vet care", "Medication")
                ),
                MasterChecklistItem(
                    id = "pet-services-subs",
                    title = "Pet Services & Subscriptions",
                    subcategory = "Pet Care Services",
                    frequency = "Monthly",
                    estimatedAmount = 3000.0,
                    description = "Dog walking, pet sitting, boarding or kennel fees during travel, pet grooming, pet insurance, and subscription boxes (e.g. BarkBox).",
                    defaultType = "Subscription",
                    examples = listOf("Dog walking", "Pet sitting", "Boarding / Kennel fees", "Pet grooming", "Pet insurance", "BarkBox")
                ),
                MasterChecklistItem(
                    id = "pet-supplies",
                    title = "Pet Supplies & Accessories",
                    subcategory = "Pet Accessories",
                    frequency = "Occasional",
                    estimatedAmount = 2000.0,
                    description = "Litter box, litter sand, leashes, harnesses, pet bedding, toys, and crates.",
                    defaultType = "Budget",
                    examples = listOf("Litter box", "Litter sand", "Leashes", "Harnesses", "Bedding", "Toys", "Crates")
                )
            )
        ),

        // 9. Smart Home, Security & Technology Upgrades / Home Improvement
        MasterChecklistCategory(
            id = "smart-home-tech",
            categoryNumber = 9,
            name = "Home Improvement, Smart Home & Tech",
            iconName = "smart_home",
            defaultFrequency = "Monthly / Occasional",
            items = listOf(
                MasterChecklistItem(
                    id = "home-security-cctv",
                    title = "Home Security & CCTV",
                    subcategory = "Security Services",
                    frequency = "Monthly",
                    estimatedAmount = 1200.0,
                    description = "Monthly monitoring services (Ring, ADT, SimpliSafe), Cloud CCTV video storage plans.",
                    defaultType = "Subscription",
                    examples = listOf("Monthly monitoring services", "Ring", "ADT", "SimpliSafe", "Cloud CCTV storage plans")
                ),
                MasterChecklistItem(
                    id = "smart-infra",
                    title = "Smart Home Infrastructure",
                    subcategory = "Home Automation",
                    frequency = "Monthly",
                    estimatedAmount = 800.0,
                    description = "Smart lighting subscriptions, automated climate control apps, and mesh Wi-Fi network rentals.",
                    defaultType = "Subscription",
                    examples = listOf("Smart lighting subscriptions", "Automated climate control apps", "Mesh Wi-Fi network rentals")
                ),
                MasterChecklistItem(
                    id = "tech-hardware-fund",
                    title = "Tech Upgrades & Hardware Fund",
                    subcategory = "Device Replacement",
                    frequency = "Occasional",
                    estimatedAmount = 5000.0,
                    description = "Device replacement budget for smartphones, laptops, tablets, battery backups, UPS, and chargers.",
                    defaultType = "Budget",
                    examples = listOf("Smartphones", "Laptops", "Tablets", "Battery backups", "Chargers", "Device replacement")
                ),
                MasterChecklistItem(
                    id = "furniture-fixtures",
                    title = "Furniture & Fixtures",
                    subcategory = "Furnishings",
                    frequency = "Occasional",
                    estimatedAmount = 12000.0,
                    description = "Sofa, bed, mattresses, dining tables, and outdoor patio furniture (purchased or rented).",
                    defaultType = "Budget",
                    examples = listOf("Sofa", "Bed", "Mattresses", "Dining tables", "Outdoor furniture")
                ),
                MasterChecklistItem(
                    id = "decor-furnishings",
                    title = "Decor & Furnishings",
                    subcategory = "Interior Decor",
                    frequency = "Occasional",
                    estimatedAmount = 5000.0,
                    description = "Curtains, carpets, rugs, bedsheets, wall art, lighting, and seasonal holiday decor.",
                    defaultType = "Budget",
                    examples = listOf("Curtains", "Carpets", "Rugs", "Bedsheets", "Wall art", "Lighting", "Seasonal decor")
                ),
                MasterChecklistItem(
                    id = "major-appliances",
                    title = "Major Appliances",
                    subcategory = "Appliances",
                    frequency = "Occasional",
                    estimatedAmount = 15000.0,
                    description = "Purchases and upgrades for washing machine, refrigerator, TV, microwave, and dishwasher.",
                    defaultType = "Budget",
                    examples = listOf("Washing machine", "Refrigerator", "TV", "Microwave", "Dishwasher")
                ),
                MasterChecklistItem(
                    id = "renovation-landscaping",
                    title = "Renovation & Landscaping",
                    subcategory = "Remodeling",
                    frequency = "Occasional",
                    estimatedAmount = 20000.0,
                    description = "Interior painting, kitchen remodels, terrace gardening, lawn care, and outdoor lighting.",
                    defaultType = "Budget",
                    examples = listOf("Interior painting", "Kitchen remodels", "Terrace gardening", "Lawn care", "Outdoor lighting")
                )
            )
        ),

        // 10. Debt & Loan Obligations
        MasterChecklistCategory(
            id = "debt-loans",
            categoryNumber = 10,
            name = "Debt & Loan Obligations",
            iconName = "credit_card",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "credit-cards",
                    title = "Credit Card Balances",
                    subcategory = "Revolving Credit",
                    frequency = "Monthly",
                    estimatedAmount = 15000.0,
                    description = "Monthly statement balances, minimum payments, or interest charges on credit cards.",
                    defaultType = "Budget",
                    examples = listOf("Credit Cards", "Monthly statement balances", "Minimum payments", "Interest charges")
                ),
                MasterChecklistItem(
                    id = "personal-loans",
                    title = "Personal Loans EMI",
                    subcategory = "Unsecured Debt",
                    frequency = "Monthly",
                    estimatedAmount = 8000.0,
                    description = "Monthly EMI repayments for unsecured personal loans.",
                    defaultType = "Budget",
                    examples = listOf("Personal Loans", "Monthly EMI", "Unsecured personal loans")
                ),
                MasterChecklistItem(
                    id = "student-loans",
                    title = "Student & Education Loans",
                    subcategory = "Education Debt",
                    frequency = "Monthly",
                    estimatedAmount = 6500.0,
                    description = "Education loan repayments including principal and interest.",
                    defaultType = "Budget",
                    examples = listOf("Student Loans", "Education loan repayments", "Principal + interest")
                ),
                MasterChecklistItem(
                    id = "auto-loans",
                    title = "Auto & Vehicle Loans",
                    subcategory = "Vehicle Financing",
                    frequency = "Monthly",
                    estimatedAmount = 12000.0,
                    description = "Car or vehicle financing monthly EMIs.",
                    defaultType = "Budget",
                    examples = listOf("Auto Loans", "Car financing EMIs", "Vehicle loan")
                ),
                MasterChecklistItem(
                    id = "bnpl-installments",
                    title = "BNPL & Installment Programs",
                    subcategory = "Short-Term Credit",
                    frequency = "Monthly",
                    estimatedAmount = 2500.0,
                    description = "Buy Now Pay Later installment programs (Klarna, Affirm, Afterpay).",
                    defaultType = "Budget",
                    examples = listOf("BNPL", "Buy Now Pay Later", "Klarna", "Affirm", "Afterpay")
                )
            )
        ),

        // 11. Remote Work & Professional Development
        MasterChecklistCategory(
            id = "remote-work",
            categoryNumber = 11,
            name = "Remote Work & Professional Expenses",
            iconName = "work",
            defaultFrequency = "Monthly / Annual",
            items = listOf(
                MasterChecklistItem(
                    id = "home-office-setup",
                    title = "Home Office Setup",
                    subcategory = "Workspace",
                    frequency = "Occasional",
                    estimatedAmount = 4000.0,
                    description = "Ergonomic chair, standing desk, monitors, webcams, and workspace lighting.",
                    defaultType = "Budget",
                    examples = listOf("Ergonomic chair", "Standing desk", "Monitors", "Webcams", "Lighting")
                ),
                MasterChecklistItem(
                    id = "professional-subs",
                    title = "Professional Subscriptions & WFH Tools",
                    subcategory = "Work Software",
                    frequency = "Monthly",
                    estimatedAmount = 2500.0,
                    description = "LinkedIn Premium, Adobe Creative Cloud, Notion, Slack, Zoom, domain hosting, websites, Canva Pro, and ChatGPT Plus or AI tools.",
                    defaultType = "Subscription",
                    examples = listOf("LinkedIn Premium", "Adobe Creative Cloud", "Notion", "Slack", "Zoom", "Domain hosting", "Websites", "Canva Pro", "ChatGPT Plus", "AI tools")
                ),
                MasterChecklistItem(
                    id = "certifications-dues",
                    title = "Certifications, Journals & Associations",
                    subcategory = "Career Growth",
                    frequency = "Annual",
                    estimatedAmount = 8000.0,
                    description = "Professional license renewals, industry-specific journal access, industry association dues, and conference tickets.",
                    defaultType = "Expense",
                    examples = listOf("Professional license renewals", "Industry association dues", "Conference tickets", "Journal access")
                ),
                MasterChecklistItem(
                    id = "office-supplies",
                    title = "Office Supplies & Shipping",
                    subcategory = "Consumables",
                    frequency = "Monthly",
                    estimatedAmount = 1000.0,
                    description = "Paper, printer ink, stationery, postage, and shipping costs.",
                    defaultType = "Budget",
                    examples = listOf("Paper", "Printer ink", "Stationery", "Postage", "Shipping costs")
                ),
                MasterChecklistItem(
                    id = "upskilling-learning",
                    title = "Upskilling & Online Learning",
                    subcategory = "Learning",
                    frequency = "Annual / Term-wise",
                    estimatedAmount = 5000.0,
                    description = "MasterClass, Coursera, Udemy, skill certifications, and professional training bootcamps.",
                    defaultType = "Subscription",
                    examples = listOf("MasterClass", "Coursera", "Udemy", "Skill certifications")
                )
            )
        ),

        // 12. Apparel, Wardrobe & Textiles
        MasterChecklistCategory(
            id = "wardrobe-textiles",
            categoryNumber = 12,
            name = "Apparel, Wardrobe & Textiles",
            iconName = "checkroom",
            defaultFrequency = "Monthly / Seasonal",
            items = listOf(
                MasterChecklistItem(
                    id = "clothing-footwear",
                    title = "Clothing & Footwear",
                    subcategory = "Apparel",
                    frequency = "Monthly / Seasonal",
                    estimatedAmount = 5000.0,
                    description = "Workwear, seasonal wardrobe updates, activewear, running shoes, and children's growing wardrobe.",
                    defaultType = "Budget",
                    examples = listOf("Workwear", "Seasonal wardrobe updates", "Activewear", "Footwear", "Children's growing wardrobe")
                ),
                MasterChecklistItem(
                    id = "garment-maintenance",
                    title = "Fabric & Garment Maintenance",
                    subcategory = "Laundry & Repairs",
                    frequency = "Monthly",
                    estimatedAmount = 1800.0,
                    description = "Dry cleaning, professional tailoring, shoe repair and polishing, and laundromat services.",
                    defaultType = "Expense",
                    examples = listOf("Dry cleaning", "Professional tailoring", "Shoe repair", "Shoe polishing", "Laundromat services")
                ),
                MasterChecklistItem(
                    id = "household-textiles",
                    title = "Household Textiles",
                    subcategory = "Linens & Decor",
                    frequency = "Occasional",
                    estimatedAmount = 3000.0,
                    description = "Periodic replacement of bed sheets, towels, curtains, and cushions.",
                    defaultType = "Budget",
                    examples = listOf("Bed sheets", "Towels", "Curtains", "Cushions")
                )
            )
        ),

        // 13. Gifting, Festivities & Charitable Giving
        MasterChecklistCategory(
            id = "gifting-festivities",
            categoryNumber = 13,
            name = "Gifting, Festivities & Charitable Giving",
            iconName = "card_giftcard",
            defaultFrequency = "Seasonal / Occasional",
            items = listOf(
                MasterChecklistItem(
                    id = "festivals-holidays",
                    title = "Festivals & Holidays",
                    subcategory = "Holiday Prep",
                    frequency = "Seasonal",
                    estimatedAmount = 8000.0,
                    description = "Festival lighting, special holiday meals, religious offerings, holiday gifting, and greeting cards.",
                    defaultType = "Budget",
                    examples = listOf("Festival lighting", "Holiday meals", "Religious offerings", "Holiday gifting", "Greeting cards")
                ),
                MasterChecklistItem(
                    id = "special-occasions",
                    title = "Special Occasions & Events",
                    subcategory = "Social Gifting",
                    frequency = "Occasional",
                    estimatedAmount = 4000.0,
                    description = "Birthday gifts, wedding gifts, baby shower gifts, housewarming gifts, and anniversary celebrations.",
                    defaultType = "Expense",
                    examples = listOf("Birthday gifts", "Wedding gifts", "Baby shower gifts", "Anniversary celebrations", "Housewarming gifts")
                ),
                MasterChecklistItem(
                    id = "charity-philanthropy",
                    title = "Charity & Religious Giving",
                    subcategory = "Giving",
                    frequency = "Monthly",
                    estimatedAmount = 3000.0,
                    description = "Monthly religious tithes, charitable organization donations, community fundraising, cause sponsorships, and non-profit memberships.",
                    defaultType = "Expense",
                    examples = listOf("Religious tithes", "Charitable donations", "Community fundraising", "Non-profit memberships")
                )
            )
        ),

        // 14. Banking, Tax & Legal Administration
        MasterChecklistCategory(
            id = "banking-legal",
            categoryNumber = 14,
            name = "Banking, Tax & Legal Administration",
            iconName = "gavel",
            defaultFrequency = "Annual / Occasional",
            items = listOf(
                MasterChecklistItem(
                    id = "banking-card-fees",
                    title = "Banking & Card Fees",
                    subcategory = "Bank Charges",
                    frequency = "Annual",
                    estimatedAmount = 2500.0,
                    description = "Credit card annual membership fees, safe deposit box fees, and international transaction fees.",
                    defaultType = "Expense",
                    examples = listOf("Credit card annual fees", "Safe deposit box fees", "International transaction fees")
                ),
                MasterChecklistItem(
                    id = "tax-preparation",
                    title = "Tax Preparation & Software",
                    subcategory = "Tax Services",
                    frequency = "Annual",
                    estimatedAmount = 4500.0,
                    description = "CPA and Accountant fees, tax filing software subscriptions (e.g. TurboTax, TaxSlayer).",
                    defaultType = "Expense",
                    examples = listOf("CPA", "Accountant fees", "Tax filing software", "TurboTax", "TaxSlayer")
                ),
                MasterChecklistItem(
                    id = "legal-admin",
                    title = "Legal & Administrative",
                    subcategory = "Legal Services",
                    frequency = "Occasional",
                    estimatedAmount = 5000.0,
                    description = "Will and estate updates, notary services, and official ID or passport renewal fees.",
                    defaultType = "Expense",
                    examples = listOf("Will", "Estate updates", "Notary services", "Official ID", "Passport renewal fees")
                )
            )
        ),

        // 15. Elderly Care & Specialized Health
        MasterChecklistCategory(
            id = "elderly-care",
            categoryNumber = 15,
            name = "Elderly Care & Specialized Health",
            iconName = "accessible",
            defaultFrequency = "Monthly",
            items = listOf(
                MasterChecklistItem(
                    id = "senior-care-services",
                    title = "Senior Care Services",
                    subcategory = "Caregiving",
                    frequency = "Monthly",
                    estimatedAmount = 15000.0,
                    description = "Assisted living fees, home nurse and caregiver salaries, and specialized physical therapy.",
                    defaultType = "Budget",
                    examples = listOf("Assisted living", "Home nurse", "Caregiver salaries", "Specialized physical therapy")
                ),
                MasterChecklistItem(
                    id = "medical-equipment",
                    title = "Medical Equipment & Supplies",
                    subcategory = "Assistive Tech",
                    frequency = "Monthly / Occasional",
                    estimatedAmount = 4000.0,
                    description = "Mobility aid rentals and maintenance, oxygen concentrators, CPAP machine supplies, and weekly pill organizer packs.",
                    defaultType = "Expense",
                    examples = listOf("Mobility aid rentals", "Oxygen concentrators", "CPAP machine supplies", "Weekly pill organizer packs")
                )
            )
        ),

        // 16. Seasonal & Outdoor Home Maintenance
        MasterChecklistCategory(
            id = "seasonal-maintenance",
            categoryNumber = 16,
            name = "Seasonal & Outdoor Home Maintenance",
            iconName = "build",
            defaultFrequency = "Seasonal / Occasional",
            items = listOf(
                MasterChecklistItem(
                    id = "lawn-garden-care",
                    title = "Lawn & Garden Care",
                    subcategory = "Landscaping",
                    frequency = "Monthly / Seasonal",
                    estimatedAmount = 2500.0,
                    description = "Mowing services, fertilizer treatment, tree trimming, and sprinkler system maintenance.",
                    defaultType = "Budget",
                    examples = listOf("Mowing services", "Fertilizer treatment", "Tree trimming", "Sprinkler system maintenance")
                ),
                MasterChecklistItem(
                    id = "seasonal-prep",
                    title = "Seasonal Household Prep",
                    subcategory = "Property Prep",
                    frequency = "Seasonal",
                    estimatedAmount = 3500.0,
                    description = "Pest control services, snow removal services, chimney sweeping, and gutter cleaning.",
                    defaultType = "Expense",
                    examples = listOf("Pest control services", "Snow removal services", "Chimney sweeping", "Gutter cleaning")
                )
            )
        ),

        // 17. Contingency & Emergency Reserves
        MasterChecklistCategory(
            id = "emergency-reserves",
            categoryNumber = 17,
            name = "Contingency & Emergency Reserves",
            iconName = "savings",
            defaultFrequency = "Monthly transfer",
            items = listOf(
                MasterChecklistItem(
                    id = "emergency-liquid-fund",
                    title = "Emergency Liquid Fund",
                    subcategory = "Safety Net",
                    frequency = "Monthly transfer",
                    estimatedAmount = 10000.0,
                    description = "Savings reserve set aside for unexpected job loss or urgent situations (3 to 6 months of expenses).",
                    defaultType = "Budget",
                    examples = listOf("Emergency Liquid Fund", "Savings reserve", "Unexpected job loss", "3-6 months expenses")
                ),
                MasterChecklistItem(
                    id = "unforeseen-repairs-fund",
                    title = "Unforeseen Home/Vehicle Repairs",
                    subcategory = "Sinking Fund",
                    frequency = "Monthly transfer",
                    estimatedAmount = 5000.0,
                    description = "Reserve fund for sudden breakdown of appliances, cars, or home infrastructure.",
                    defaultType = "Budget",
                    examples = listOf("Unforeseen Home/Vehicle Repairs", "Sudden breakdown of appliances", "Cars", "Home infrastructure")
                ),
                MasterChecklistItem(
                    id = "medical-contingency-fund",
                    title = "Medical Contingency Fund",
                    subcategory = "Health Reserve",
                    frequency = "Monthly transfer",
                    estimatedAmount = 5000.0,
                    description = "Extra out-of-pocket medical reserve for non-insured medical treatments or health deductibles.",
                    defaultType = "Budget",
                    examples = listOf("Medical Contingency Fund", "Out-of-pocket medical reserve", "Non-insured medical treatments")
                )
            )
        )
    )

    fun getAllCategories(): List<MasterChecklistCategory> = categories

    fun searchItems(query: String): List<Pair<MasterChecklistCategory, MasterChecklistItem>> {
        if (query.isBlank()) return emptyList()
        val result = mutableListOf<Pair<MasterChecklistCategory, MasterChecklistItem>>()
        val cleanQuery = query.lowercase().trim()
        val keywords = cleanQuery.split(Regex("\\s+")).filter { it.length > 1 }

        categories.forEach { cat ->
            cat.items.forEach { item ->
                val fullText = (
                    cat.name + " " +
                    item.title + " " +
                    item.subcategory + " " +
                    item.description + " " +
                    item.examples.joinToString(" ")
                ).lowercase()

                val isExactMatch = fullText.contains(cleanQuery)
                val isKeywordMatch = keywords.isNotEmpty() && keywords.any { fullText.contains(it) }

                if (isExactMatch || isKeywordMatch) {
                    result.add(Pair(cat, item))
                }
            }
        }
        return result
    }
}
