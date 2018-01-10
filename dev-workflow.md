# Git Development Workflow Using Feature Branch WorkFlow
I propose we use the Feature Branch workflow which currently matches our
development process, mono repo setup, test driven development and continuous delivery.

Locally we'll have a dated master copy as well as branch of the
feature or bugfix we are working on.

Proceed with Git flows as normal then submit a Pull Request to Francois or Roger who will merge with the master branch..

# Overview of Feature Branch Workflow
## First clone the repository into your local folder.
`git checkout master
git fetch origin
git reset --hard origin/master
`

## Create and Name your branch a descriptive name to explain what is being worked on
`git checkout -b feature-fixbug-0001`

## Committing your branch
`git status
git add <files>
git commit
`

## Push the branch along the central repository
`git push -u origin feature-fixbug-0001`

## Getting Feedback and PR
- Submit a Pull Request in Github and add Reviewer to merge
- Resolve the comments, feedback and testing
- Push your changes to the Pull Request
- Resolve further feedback.

## Once approved then merge with master
`git checkout master
git pull
git pull origin marys-feature
git push`

#Testing - Ideas to be confirmed...
- High Level Acceptance Test
- Component Unit Tests
