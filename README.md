# Wordle-Style Word Guessing Game

A beautiful Wordle-inspired word guessing game with a clean, modern interface!

## Features
- 🎯 **Wordle-style Grid**: 6x5 grid with color-coded feedback
- 🎨 **Clean Design**: White background with Wordle's signature colors
- 👤 **User System**: Registration, login, and role management
- 👑 **Admin Statistics**: View game statistics and reports
- ⌨️ **Keyboard Support**: Press Enter to submit guesses
- 🎮 **Interactive**: Real-time grid updates with color feedback

## How to Play
1. Register a new account or login
2. Click "START" to begin a new game
3. Type your 5-letter guess and press Enter or click "SUBMIT"
4. Watch the grid update with colors:
   - 🟩 **Green**: Correct letter in correct position
   - 🟨 **Yellow**: Correct letter in wrong position
   - ⬜ **Gray**: Letter not in word
5. You have 6 guesses to find the word
6. Maximum 3 games per day per user

## Admin Features
- Login as ADMIN to view statistics
- See total users, games, win rates
- View recent game history

## How to Run
```bash
javac *.java
java WordGame
```

## Files
- `WordGame.java` - Main Wordle-style game application
- `User.java` - User data model
- `GameResult.java` - Game result data model
- `users.txt` - User data storage
- `games.txt` - Game results storage

## Wordle-Style Features
- **6x5 Grid Layout**: Just like the original Wordle
- **Color Feedback**: Green, yellow, and gray squares
- **Clean Typography**: Bold letters in grid squares
- **Keyboard Input**: Type and press Enter to submit
- **Status Updates**: Real-time feedback on guesses

Enjoy playing this Wordle-inspired game! 🎮✨
