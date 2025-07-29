Where to find Sweden's public holiday information?
- https://date.nager.at/PublicHoliday/Sweden/2013

Should public holidays be loaded from CSV or to integrate date.nager API?
- CSV approach is more suitable for this project.

How to read CSV files and save them to Java objects?
- OpenCSV (https://opencsv.sourceforge.net/apidocs/com/opencsv/CSVReader.html)

What would be the best way to externalize application parameters?
Should the parameters be loaded from config file or from database?
- YAML config file -> simplicity.
- Database -> good if the rules will be updated via UI.

Loading configuration best practices?
- https://medium.com/@lavanyabhimavaram/mastering-properties-and-configuration-in-spring-boot-63a2373e3c9b

Java Stream function for date range?
- datesUntil()

What would be the simplest and most logical data model for this application?
- Camera records vehicle's registration and saves it to database together with timestamp. (Passage table)
- Vehicle should have registration as primary key and a defined type. (Vehicle table)

What endpoints will users most likely need?
- Calculate tax for a given registration and date or date range.
- Calculate tax for all vehicles that passed in given date period.
- Save new passages and vehicles.