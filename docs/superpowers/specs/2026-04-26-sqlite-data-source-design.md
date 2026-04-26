# SQLite Data Source Cleanup Design

## Goal
Move remaining runtime/business/sample data out of Java/XML hardcoded lists and into SQLite-backed data flows. Seed data may live in an asset file, but app screens should read business data from SQLite.

## Scope

### In scope
- Move seed/sample records from `SeedDataHelper.java` into `app/src/main/assets/seed_data.json`.
- Keep `SeedDataHelper` as the importer/normalizer that reads the asset and inserts or updates SQLite idempotently.
- Update table selection flows to read tables from `ban_an` instead of generating `Bàn 01..Bàn 20` in code.
- Update home categories to derive categories from SQLite dishes instead of static Java list.
- Update quick login to resolve demo accounts from SQLite instead of hardcoded credential constants in `DangNhapActivity`.
- Add tests/source checks for the removed hardcoded runtime data.

### Out of scope
- Changing the UI design.
- Removing UI labels/copy from `strings.xml`.
- Replacing SQLite with a server database.
- Reworking authentication security beyond existing demo-login behavior.

## Architecture

### Asset seed source
Create `app/src/main/assets/seed_data.json` with sections:
- `test_users`
- `additional_users`
- `dishes`
- `additional_dishes`
- `tables`
- `reservations`
- `orders`
- `service_requests`

The JSON contains business/sample values. Java code should not contain sample names, emails, reservation codes, order codes, or table lists.

### Seed importer
`SeedDataHelper` will:
1. Read `seed_data.json` from assets.
2. Parse JSON with Android's built-in JSON APIs.
3. Insert/update rows using existing SQLite helper methods.
4. Preserve idempotency by matching existing rows by natural keys such as email/phone, dish name, table code/name, reservation code, and order code.

### Runtime data reads
- `DatBanFragment`, `DatBanNoiBoFragment`, and `GioHangActivity` read table names from `databaseHelper.layTatCaBanAn()`.
- `TrangChuFragment` reads distinct categories from dishes via a new database method.
- `DangNhapActivity` asks the database for demo account credentials by role/customer type instead of storing email/password constants.

## Error handling
- If the seed asset cannot be read or parsed, log the error and avoid crashing app startup where possible.
- If a section is missing, skip that section.
- If a record is invalid, skip it or fail that record only.

## Testing
Add/update unit/source tests to verify:
- `SeedDataHelper.java` no longer contains direct sample dish/user/table/order/reservation/request records.
- Runtime table selectors do not generate `1..20` tables.
- Home categories are loaded from database categories.
- Quick login has no hardcoded demo credential constants.
- Existing admin account SQLite tests still pass.

## Verification
Run:
- `JAVA_HOME="/opt/android-studio/jbr" ./gradlew testDebugUnitTest`
- `JAVA_HOME="/opt/android-studio/jbr" ./gradlew assembleDebug`
