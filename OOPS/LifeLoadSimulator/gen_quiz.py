import random

topics = [
    "Compound Interest", "Inflation", "Credit Score", "Budgeting", "Investing", 
    "Savings", "Debt", "Taxes", "Retirement", "Insurance", "Emergency Fund",
    "Net Worth", "Assets", "Liabilities", "Stocks", "Bonds", "Mutual Funds",
    "ETF", "401k", "IRA", "Mortgage", "Interest Rates", "Liquidity",
    "Diversification", "Risk Tolerance", "Bear Market", "Bull Market", "Volatility",
    "Capital Gains", "Dividends", "Passive Income", "Active Income", "FICO Score",
    "Bankruptcy", "Auditing", "Inflation Rate", "GDP", "Recession", "Deflation",
    "Arbitrage", "Annuity", "Depreciation", "Escrow", "Foreclosure", "Liability",
    "Principal", "Variable Rate", "Fixed Rate", "Yield", "Zero-Coupon Bond",
    "Amortization", "Appraisal", "Back-end Load", "Basis Point", "Blue Chip",
    "Cash Flow", "Closing Costs", "Collateral", "Credit Limit", "Debit Card",
    "Default", "Equity", "Expense Ratio", "Face Value", "Gross Income",
    "Hedge Fund", "Index Fund", "Individual Contributor", "Insolvency", "Junk Bond",
    "Keogh Plan", "Large-Cap", "Leverage", "Margin", "Money Market",
    "Nasdaq", "Over-the-Counter", "Penny Stock", "Portfolio", "Preferred Stock",
    "Price-to-Earnings", "Profit and Loss", "Proxy", "Real Interest Rate", "S&P 500",
    "Securities", "Short Selling", "Small-Cap", "Social Security", "Standard Deviation",
    "Stock Split", "Tax Bracket", "Tax Credit", "Tax Deduction", "Time Value of Money",
    "Treasury Bill", "Unemployment Rate", "Vesting", "W-2 Form", "Working Capital"
]

templates = [
    ("What is the primary effect of {} on savings?", "It changes the value over time", "It has no effect", "It makes banks rich", "It is illegal"),
    ("Why should one understand {}?", "To make informed financial decisions", "To win at board games", "To impress strangers", "To get free money"),
    ("High {} usually leads to...", "Increased financial complexity", "Lower stress", "Instant wealth", "Free housing"),
    ("Which of these is directly impacted by {}?", "Personal purchasing power", "The weather", "Ocean tides", "Star positions"),
    ("When managing {}, one should always...", "Research and plan carefully", "Trust random internet advice", "Spend all available cash", "Ignore it completely"),
    ("A common misconception about {} is that...", "It only applies to the rich", "It is a type of food", "It is 100% risk-free", "It happens overnight"),
    ("The best time to start thinking about {} is...", "As early as possible", "The day before retirement", "Never", "When you are 90"),
    ("What role does {} play in a balanced portfolio?", "It provides specific risk management", "It is just for show", "It acts as a placeholder", "It guarantees 1000% returns"),
    ("If {} increases significantly, what usually happens?", "Market adjustments occur", "Everyone gets a free car", "Time stops", "Banks close forever"),
    ("How does {} affect a young professional?", "It shapes their long-term growth", "It doesn't matter until age 50", "It gives them super powers", "It makes them taller")
]

questions = []
for topic in topics:
    for tpl in templates:
        q_text = tpl[0].format(topic)
        questions.append([q_text, tpl[1], tpl[2], tpl[3], tpl[4]])

with open('lifeload-client/src/main/java/com/lifeload/client/ui/QuizData.java', 'w', encoding='utf-8') as f:
    f.write('package com.lifeload.client.ui;\n\n')
    f.write('public class QuizData {\n')
    f.write('    public static final String[][] ALL_QUESTIONS = {\n')
    for q in questions[:1000]: # Exactly 1000
        line = '        {"' + q[0] + '", "' + q[1] + '", "' + q[2] + '", "' + q[3] + '", "' + q[4] + '", "0"},\n'
        f.write(line)
    f.write('    };\n')
    f.write('}\n')

print(f"Generated {len(questions)} questions in QuizData.java")
