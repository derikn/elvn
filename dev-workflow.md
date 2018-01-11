# Git Development Workflow Using Feature Branch WorkFlow
I propose we use the Feature Branch workflow which currently matches our
development process, mono repo setup, test driven development and continuous delivery.

Locally we'll have a dated master copy as well as branch of the
feature or bugfix we are working on.

Proceed with Git flows as normal then submit a Pull Request to Francois or Roger who will merge with the master branch..

#OS Fork Model vs Shared Repository
Common Open Source Libraries like jQuery, React and others use a fork Model
where the contributor will fork the main repository from main_repo/repo to forked_repo/repo. Branches are then created on the forked_repo/repo/branch and pull requested against main_repo/repo/master.

- Contributors must fork the repo to begin development
- Only Maintainers can create main_repo/repo Branches
- Only Maintainers can review and merge pull requests
- There will be a bunch of forked elvns and might result in lots of remotes
when collaborating and heavily iterating.

###Shared Repository
Currently in our team of 5 Developers we can use a shared repository model that just uses the main_repo/repo and its branches. However this will require everyone to have maintainer access to the repo.

- Anyone can create a branch on the main repo to begin development
- You cannot approve your own Pull Request (ensures 2 sets of eyes)
- Maintains the Monorepo setup
- Multiple contributors can iterate quickly on one branch in the master

#Pull Request Review Process

# Overview of Feature Branch Workflow
## First clone the repository into your local folder.
`git checkout master`

`git fetch origin`

`git reset --hard origin/master`


## Create and Name your branch a descriptive name to explain what is being worked on
`git checkout -b feature-fixbug-0001`

## Committing your branch
`git status`

`git add <files>`

`git commit`



## Push the branch along the central repository
`git push -u origin feature-fixbug-0001`

## Getting Feedback and PR
- Submit a Pull Request in Github and add Reviewer to merge
- Resolve the comments, feedback and testing
- Push your changes to the Pull Request
- Resolve further feedback.

## Once approved then merge with master
`git checkout master`

`git pull`

`git pull origin feature-fixbug-0001`

`git push`

#Testing - Ideas to be confirmed...
- High Level Acceptance Test
- Component Unit Tests
