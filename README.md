ArgumentationDebates
======================

Argumentation is a form of **reasoning** which is usually employed by humans during their interactions.
Interestingly, it is also possible to build A.I. systems where **software agents (or robots) exchange arguments** in order to achieve their goals
(e.g. to reach a good collective decision).

This Java project contains all the necessary classes in order to define and implement a **multi-agent argumentation debate**.
The type of argumentation supported is **abstract argumentation**.
An abstract argumentation system consists, basically, of a set of arguments and some attacks & supports between pairs of arguments,
while argument content and structure are not analyzed.

Using the classes of this project, we are able to:

1. Define abstract argumentation systems with attacks and supports.
2. Compute argument acceptability according to different approaches in the literature (e.g. grounded semantics, numerical argument evaluation).
3. Define a set of debating agents with their attributes: their personal beliefs (in the form of abstract argumentation systems),
their expertise, their goals in a debate, their debating strategies.
4. Simulate a multi-agent argumentation debate.

The classes of this project have already been used in two peer-reviewed publications:

1. **Empirical Evaluation of Strategies for Multiparty Argumentative Debates** (in **CLIMA'14**):
In this work, different agent strategies have been defined, multi-agent debates have been simulated, and finally
the strategies have been compared and evaluated.
2. **Identifying Malicious Behavior in Multi-party Bipolar Argumentation Debates** (in **EUMAS'15**):
In this work, some "malicious" agents may strategically lie and hide information, so we propose a method in order to identify such agents.
