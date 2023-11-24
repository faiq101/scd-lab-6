import java.io.*;
import java.util.ArrayList;
import java.util.List;

class User {
    private int id;
    private String username;
    private List<Post> posts;
    private List<Comment> comments;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        this.posts = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void createPost(String content) {
        Post post = new Post(this, content);
        posts.add(post);
    }

    public void commentOnPost(Post post, String text) {
        Comment comment = new Comment(this, post, text);
        comments.add(comment);
    }

    public List<Post> getPosts() {
        return posts;
    }

    // Save user profiles to a text file with exception handling
    public void saveProfileToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_profiles.txt", true))) {
            writer.write(id + "," + username);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving user profile to file: " + e.getMessage());
        }
    }

    // Load user profiles from a text file with exception handling
    public static List<User> loadProfilesFromFile() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("user_profiles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String username = parts[1];
                User user = new User(id, username);
                users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error loading user profiles from file: " + e.getMessage());
        }
        return users;
    }
}

class Post {
    private User author;
    private String content;
    private List<Comment> comments;

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    // Save posts to a text file with exception handling
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("posts.txt", true))) {
            writer.write(author.getId() + "," + content);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving post to file: " + e.getMessage());
        }
    }

    // Load posts from a text file with exception handling
    public static List<Post> loadPostsFromFile(List<User> users) {
        List<Post> posts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("posts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int authorId = Integer.parseInt(parts[0]);
                String content = parts[1];
                User author = users.stream().filter(u -> u.getId() == authorId).findFirst().orElse(null);
                if (author != null) {
                    Post post = new Post(author, content);
                    posts.add(post);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading posts from file: " + e.getMessage());
        }
        return posts;
    }
}

class Comment {
    private User author;
    private Post post;
    private String text;

    public Comment(User author, Post post, String text) {
        this.author = author;
        this.post = post;
        this.text = text;
    }

    // Save comments to a text file with exception handling
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("comments.txt", true))) {
            writer.write(author.getId() + "," + post.author.getId + "," + text);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving comment to file: " + e.getMessage());
        }
    }

    // Load comments from a text file with exception handling
    public static List<Comment> loadCommentsFromFile(List<User> users, List<Post> posts) {
        List<Comment> comments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("comments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int authorId = Integer.parseInt(parts[0]);
                int postId = Integer.parseInt(parts[1]);
                String text = parts[2];
                User author = users.stream().filter(u -> u.getId() == authorId).findFirst().orElse(null);
                Post post = posts.stream().filter(p -> p.author.getId() == postId).findFirst().orElse(null);
                if (author != null && post != null) {
                    Comment comment = new Comment(author, post, text);
                    comments.add(comment);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading comments from file: " + e.getMessage());
        }
        return comments;
    }
}

public class InstagramApp {
    public static void main(String[] args) {
        try {
            List<User> users = User.loadProfilesFromFile();

            List<Post> posts = Post.loadPostsFromFile(users);

            List<Comment> comments = Comment.loadCommentsFromFile(users, posts);
            if (users.isEmpty()) {
                User user1 = new User(1, "user1");
                User user2 = new User(2, "user2");
                users.add(user1);
                users.add(user2);
                user1.saveProfileToFile();
                user2.saveProfileToFile();
            }

            // User 1 creates a post
            users.get(0).createPost("This is my first post!");
            posts.get(0).saveToFile();

            // User 2 comments on User 1's post
            users.get(1).commentOnPost(posts.get(0), "Nice post!");
            comments.get(0).saveToFile();

            // Display User 1's posts and comments
            System.out.println("User: " + users.get(0).getUsername());
            for (Post post : users.get(0).getPosts()) {
                System.out.println("Post: " + post.getContent());
                for (Comment comment : post.getComments()) {
                    System.out.println("Comment: " + comment.getText());
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}