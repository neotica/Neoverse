package id.neotica.world.utils

const val rockPicture = "https://firebasestorage.googleapis.com/v0/b/neoverse-neotica.appspot.com/o/resPicture%2Fob_rock.png?alt=media&token=2ff0a43d-6ed5-4867-9086-1b1ebe7fd8be"
const val bgTavern = "https://firebasestorage.googleapis.com/v0/b/neoverse-neotica.appspot.com/o/resPicture%2Fbg_tavern.png?alt=media&token=b05998eb-fd5a-4306-9293-c2c93932782d"
const val bgMining = "https://firebasestorage.googleapis.com/v0/b/neoverse-neotica.appspot.com/o/resPicture%2Fbg_mining.png?alt=media&token=46c50a41-71ca-42df-a5ae-662f3a042f99"
const val bgLeaderBoards = "https://firebasestorage.googleapis.com/v0/b/neoverse-neotica.appspot.com/o/resPicture%2Fbg_leaderboards.png?alt=media&token=f272c5e2-33b0-45bd-aecb-d6f847e60d06"
const val worldIntro = "Welcome to the World Section! Ready to step into a realm of wonder? \uD83C\uDFF0 Venture through the mystical portal into Peking Town, a charming village brimming with secrets and adventure. \uD83D\uDDFA\uFE0F Your epic quest begins here. Tap the button below to unlock the portal and claim your aura!"
const val worldIntro2 = "Welcome, weary traveler. Dare to venture beyond this veil into the shadow-kissed town of Grimhaven. \uD83C\uDFF0 Here, whispers of forgotten magic and tales of ancient curses linger in the air. \uD83D\uDDFA\uFE0F Embrace the darkness and claim your destiny. Tap the button below to unlock the portal and awaken your true self."

//cmd
const val helpCmd = """This is a command tool program made for the Neoverse app by Neotica.
Copyright, 2024 Neotica Digital Solusi.

Here is the list of commands:
- help: Display this help message
- help-math: Display lists of arithmetics commands
- help-goto: Display lists of goto commands
- goto: Navigate to any page you want (e.g., goto profile)
- clear: Clears the terminal logs.
- exit: exit the terminal
"""

val helpGoto = """Help goto
This is a list of goto commands
- ${GotoCommand().home}
- ${GotoCommand().profile}
- ${GotoCommand().rootHome}
- ${GotoCommand().tavern}
- ${GotoCommand().leaderboards}
- ${GotoCommand().rootWorld}

How to use: "goto home" will navigate you to home screen.
"""

class GotoCommand {
    val home = "home"
    val profile = "profile"
    val rootHome = "roothome"
    val rootAuth = "rootauth"
    val rootWorld = "rootworld"
    val addChat = "addchat"
    val tavern = "tavern"
    val leaderboards = "leaderboards"
    val superAdmin = "superadmin"
    val raven = "raven"
}

const val helpMath = """Help Math
This is a list of arithmetic commands
- '+' : Addition
- '-' : Subtraction
- * : Multiplication
- '/' : Division

How to use: "1+1" will output to "2"
Do not use white space."""