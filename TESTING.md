# Testing - Student Academic & Course Management System

All results below were produced on Windows 11 against a **live MySQL 8.4
server**, not by inspection. Testing uses two plain-Java harnesses (no JUnit,
no build tool) that drive the same service layer and Swing screens as the
application.

## Environment

| Component | Value |
|-----------|-------|
| OS | Windows 11 |
| JDK | OpenJDK 21.0.10 (`javac` 21.0.10) |
| Database | MySQL 8.4 on `127.0.0.1:3306` |
| Database account | `registrar` / `registrar`, ALL privileges on `student_academic_management` |
| JDBC driver | `lib/mysql-connector-j-8.3.0.jar` (Connector/J 8.3.0) |
| Connection config | `config/db.properties` (gitignored) |
| UI | Swing, custom "Registrar" design system, no third-party libraries |

## Test harnesses

### `test/smoke/CrudSmoke.java`

A service-layer lifecycle test against the real schema. It creates a temporary
faculty member, student and course with timestamp-derived identifiers, enrolls
the student, loads the attendance roster, attempts duplicate and invalid
operations, records attendance and a result, updates every record, then deletes
everything in reverse dependency order. Cleanup also runs from a `finally`
block, so a failed assertion cannot leave rows behind.

### `test/ui/UiSmoke.java`

A deterministic Swing smoke test. It opens `MainFrame`, navigates to every
module, selects a table row, loads the attendance roster, opens all six report
views, and writes 15 PNGs to `build/ui-smoke/`.

> **Screenshot note:** the PNGs are painted offscreen. On a HiDPI desktop the
> offscreen font metrics differ slightly from the live window's hinted
> metrics, so the final glyph of some labels can look clipped in the PNGs. The
> running window renders those labels fully; the screenshots are intended for
> layout and data checks.

## Build and run

From the project root in PowerShell:

```powershell
javac -d build -cp "lib\mysql-connector-j-8.3.0.jar" `
  src\app\*.java src\model\*.java src\dao\*.java src\service\*.java `
  src\db\*.java src\ui\*.java src\ui\components\*.java src\util\*.java

javac -d build -cp "build;lib\mysql-connector-j-8.3.0.jar" `
  test\ui\UiSmoke.java test\smoke\CrudSmoke.java
```

Run the lifecycle test:

```powershell
java -cp "build;lib\mysql-connector-j-8.3.0.jar" smoke.CrudSmoke
```

Run the UI smoke test:

```powershell
java -cp "build;lib\mysql-connector-j-8.3.0.jar" ui.UiSmoke
```

Launch the application:

```powershell
java -cp "build;lib\mysql-connector-j-8.3.0.jar" app.Main
```

## CrudSmoke result

Latest run:

```text
CRUD_RESULT passed=40 failed=0
```

The 40 checks cover every layer of the lifecycle:

| Phase | Checks |
|-------|--------|
| Create | faculty created, student created, course created, unassigned course created |
| Enroll | enrollment created, roster size, roster student code, roster student name |
| Reports | courses-by-faculty size and course code with an unassigned course in the table, students-in-course size, student code, student name, completed enrollment included |
| Reject | duplicate student code, duplicate active enrollment, delete student with enrollment, marks above maximum, non-numeric marks |
| Attendance | percentage after ABSENT (0%), after PRESENT (100%), after LATE (100%) |
| Results | result created, marks 85, percentage 85.00, grade B, marks updated to 92, grade updated to A |
| Update | student name, student department, faculty designation, faculty specialization, course name, course credits, enrollment status |
| Delete | enrollment, course, unassigned course, student, faculty all removed and confirmed absent |

The attendance rule is verified explicitly: `PRESENT` and `LATE` count as
present, `ABSENT` does not. The result is an upsert: saving the same enrollment
again updates the existing row instead of creating a duplicate.

After the run, the database is back to its original sample state:

| Table | Rows |
|-------|------|
| students | 3 |
| faculty | 2 |
| courses | 3 |
| enrollments | 5 |
| results | 3 |
| attendance | 5 |

## UiSmoke result

All 15 captures completed with `SMOKE_DONE`:

| Screenshot | Screen / state |
|------------|----------------|
| `smoke-dashboard.png` | Dashboard with live KPIs, grade mix and recent tables |
| `smoke-students-empty.png` | Students register before a row is selected |
| `smoke-students-selected.png` | Students register with the editor populated |
| `smoke-faculty.png` | Faculty register and editor |
| `smoke-courses.png` | Courses register and editor |
| `smoke-enrollments.png` | Enrollment composer and ledger |
| `smoke-attendance-empty.png` | Attendance before loading a roster |
| `smoke-attendance-roster.png` | Attendance roster with status choices |
| `smoke-results.png` | Results composer and result ledger |
| `smoke-report-directory.png` | Report library, Student directory view |
| `smoke-report-students-course.png` | Report library, Students in a course view |
| `smoke-report-faculty-courses.png` | Report library, Courses by faculty view |
| `smoke-report-attendance.png` | Report library, Course attendance view |
| `smoke-report-results.png` | Report library, Course results view |
| `smoke-report-overall.png` | Report library, Overall performance view |

Verified in the screenshots and the running window:

- Dashboard KPIs match MySQL: 3 students, 2 faculty, 3 courses, 5 enrollments,
  80.00% attendance, 85.00% academic average, and the A/B/C/D/F grade mix.
- All six module screens navigate and load without an exception.
- Tables fit their cards; emails ellipsize; hidden primary-key columns still
  select the correct row.
- Count chips appear only when a count is present; combo boxes keep their
  rounded borders and chevrons.
- Attendance shows the enrolled student's code and name, not a raw ID.
- Results show readable enrollment labels (`STU0001 · Aarav Singh · CS101`).
- Students in a course shows student code, name, enrollment status and date.
- Courses by faculty resolves assignments without an NPE when other courses
  have no faculty assigned.
- Course results headers (`Marks`, `Max`, `Pass`, `Percent`, `Grade`) fit their
  columns without truncation.

## Defects found and fixed during testing

| Defect | Fix |
|--------|-----|
| `FacultyService.create` called `findById(0)` and threw before the dead `&& false` short-circuit | Removed the dead block |
| Saving a result returned no new id; a second save failed with a duplicate-key error | `ResultDAO.save` returns the generated key; `ResultService.save` loads an existing result id and performs an update |
| Attendance roster lacked student code and name | `EnrollmentDAO.findRoster` joins students and enrollments |
| Attendance summary and several report loaders failed on empty/partial data | Added null-safe loaders and empty states |
| Hidden ID columns caused the wrong row to be edited | Row selection maps through the model's hidden id column |
| Count-chip discs rendered blank on plain data cards | Chip is hidden until a non-blank count is set |
| Combo-box borders were painted over by the opaque current-value renderer | Renderer is non-opaque for the selected index; the outline is drawn after `super.paintComponent` |
| Faculty search placeholder and student-directory columns were cramped | Tightened column sets and ellipsizing renderers |
| Courses by faculty threw `NullPointerException` when any course had no assigned faculty | Null-safe `Integer.valueOf(facultyId).equals(...)` comparison instead of unboxing |
| Students in a course showed raw enrollment and student IDs | New `EnrollmentDAO.findReportByCourse` join returns student code, name, date and status |
| Course results headers were truncated (`MARKS OBT...`, `PASS MA...`) | Shortened to `Marks`, `Max`, `Pass`, `Percent` and rebalanced widths |

## Reproduce from scratch

1. Start MySQL and create the schema:
   ```powershell
   mysql -u root -p < sql\schema.sql
   mysql -u root -p student_academic_management < sql\sample-data.sql
   ```
   Replace `root` with the MySQL account you use. The schema is safe to re-run.
2. Create `config/db.properties` from `config/db.properties.example` and point
   it at a MySQL account with access to `student_academic_management`.
3. Compile the application and the two harnesses with the commands above.
4. Run `smoke.CrudSmoke`, then `ui.UiSmoke`.
5. Start the app with `app.Main` or `RUN_LOCALLY.bat`.

## Notes

- The harnesses are compiled into `build/`, which is gitignored along with the
  generated PNGs. No test data, credentials or screenshots are committed.
- `CrudSmoke` is safe to re-run: identifiers include a timestamp and cleanup
  removes only the rows it created.
- The application and both harnesses use the same `config/db.properties`, so a
  successful harness run proves the app is pointed at a working database.
