# Test Catalog & Registry

## Purpose
Single source of truth for all test types, locations, tags, and reusability guidelines.

## Test Types & Locations
| Type              | Location                        | Tag         |
|-------------------|---------------------------------|-------------|
| Unit              | `src/test` (per module)         | fast        |
| Integration       | `src/test` (per module)         | integration |
| API Contract      | `src/test` (per module)         | api         |
| Security          | `src/test` (per module)         | security    |
| Performance       | `performance-tests/src/test`    | performance |
| E2E               | `integration-tests/src/test`    | e2e         |
| Chaos             | `integration-tests/src/test`    | chaos       |

## Guidelines
- Use semantic tags for selective execution
- Ensure tests are reusable and cataloged
- Document test ownership and environment

