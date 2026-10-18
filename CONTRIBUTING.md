# Contributing Guide

Thank you for your interest in contributing to the Student Academic and Course Management System!

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to the project maintainers.

## How to Contribute

### Reporting Bugs
1. Check existing issues first
2. Create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots (if applicable)
   - Environment details (OS, Java/Node versions)

### Suggesting Enhancements
1. Open an issue with "Enhancement" label
2. Describe the feature and use case
3. Explain why it would be valuable

### Pull Requests

#### Before Submitting
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Run tests: `./mvnw test` (backend) / `npm test` (frontend)
5. Run linters: `./mvnw spotless:check` / `npm run lint`
6. Commit with clear messages
7. Push to your fork
7. Open Pull Request

#### PR Requirements
- Clear title and description
- Reference related issues
- Include tests for new features
- Update documentation if needed
- Pass all CI checks

## Development Setup

### Backend
```bash
cd backend
# Prerequisites: Java 21, Maven 3.9+, MySQL 8.0+
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
# Prerequisites: Node.js 18+, npm
npm install
npm run dev
```

## Coding Standards

### Backend (Java)
- Follow Google Java Style Guide
- Use Lombok for boilerplate reduction
- Write meaningful Javadoc for public APIs
- Prefer constructor injection over field injection
- Use `@Valid` for request validation
- Handle exceptions in `GlobalExceptionHandler`

### Frontend (TypeScript/React)
- Use TypeScript strict mode
- Follow React Hooks rules
- Use functional components with hooks
- Follow Tailwind CSS conventions
- Use React Hook Form for forms
- Use TanStack Query for server state

### Git Commit Messages
```
feat: add student enrollment feature
fix: fix attendance percentage calculation
docs: update API documentation
refactor: simplify enrollment service
test: add enrollment service tests
chore: update dependencies
```

Format: `<type>(<scope>): <description>`

Types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `perf`, `style`

## Pull Request Template

```markdown
## Description
Brief description of changes

## Related Issues
Closes #123

## Changes Made
- Added student enrollment validation
- Fixed attendance percentage calculation
- Added unit tests for enrollment service

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
[Add screenshots]

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
```

## Code Review Guidelines

### Reviewers Should Check
- Code correctness and logic
- Test coverage
- Performance implications
- Security considerations
- Documentation updates
- Coding standards compliance

### Authors Should
- Keep PRs small and focused
- Respond to feedback promptly
- Update PR based on feedback
- Ensure CI passes

## Release Process

### Versioning
We follow Semantic Versioning (MAJOR.MINOR.PATCH):
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes (backward compatible)

### Release Steps
1. Update version in `pom.xml` and `package.json`
2. Update `CHANGELOG.md`
3. Create release tag: `git tag v1.2.0`
4. Push tag: `git push origin v1.2.0`
5. CI builds and publishes artifacts

## Getting Help

- **Documentation**: Check `docs/` directory
- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Email**: maintainers@student-mgmt.example.com

## Recognition

Contributors are recognized in:
- `CONTRIBUTORS.md` file
- Release notes
- Project README

Thank you for contributing!