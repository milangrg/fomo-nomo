package learn.fomo_nomo.data;

import learn.fomo_nomo.data.mappers.EventMapper;
import learn.fomo_nomo.models.Event;
import learn.fomo_nomo.models.EventType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class EventJdbcTemplateRepository implements EventRepository{

    private final JdbcTemplate jdbcTemplate;

    public EventJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> findAll() {

            final String sql = "select event_id, "
                    + "e.user_id, u.first_name, u.last_name, u.email, u.phone, u.dob, "
                    + "title, description, "
                    + "e.location_id, l.address, l.state, l.city, l.postal, l.location_name, "
                    + "event_type, `start`, `end` "
                    + "from event e "
                    + "inner join `user` u on u.user_id = e.user_id "
                    + "inner join location l on l.location_id = e.location_id;";
        return jdbcTemplate.query(sql,new EventMapper());
    }

    @Override
    @Transactional
    public Event findById(int eventId) {
        final String sql = "select event_id, "
                + "e.user_id, u.first_name, u.last_name, u.email, u.phone, u.dob, "
                + "title, description, "
                + "e.location_id, l.address, l.state, l.city, l.postal, l.location_name, "
                + "event_type, `start`, `end` "
                + "from event e "
                + "inner join `user` u on u.user_id = e.user_id "
                + "inner join location l on l.location_id = e.location_id "
                + "where event_id = ?;";

        Event result = jdbcTemplate.query(sql,new EventMapper(),eventId).stream()
                .findAny().orElse(null);

        return result;
    }

    @Override
    public Event add(Event event) {
        final String sql = "insert into event (user_id, title, description, location_id, event_type, start, end)"
                + "values (?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, event.getHost().getUserId());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setInt(4, event.getLocation().getLocationId());
            ps.setString(5, event.getEventType().getName());
            ps.setTimestamp(6, Timestamp.valueOf(event.getStart()));
            ps.setTimestamp(7, Timestamp.valueOf(event.getEnd()));
            return ps;
        },keyHolder);

        if (rowsAffected <= 0){
            return null;
        }

        event.setEventId(keyHolder.getKey().intValue());
        return event;

    }

    @Override
    public boolean update(Event event) {
        final String sql = "update event set "
                + "title = ?, "
                + "description = ?, "
                + "location_id = ?, "
                + "event_type = ?, "
                + "start = ?, "
                + "end = ? "
                + "where event_id = ?";

        return jdbcTemplate.update(sql,
                event.getTitle(),
                event.getDescription(),
                event.getLocation().getLocationId(),
                event.getEventType().getName(),
                event.getStart(),
                event.getEnd(),
                event.getEventId()) > 0;
    }

    @Override
    @Transactional
    public boolean delete(int eventId) {
        jdbcTemplate.update("delete from invitation where event_id = ?",eventId);
        return jdbcTemplate.update("delete from event where event_id = ?",eventId) > 0;
    }
}
